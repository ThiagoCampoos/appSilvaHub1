package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.util.DateUtils
import com.example.silvahub.util.Money

class RegistrarCompraCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    suspend operator fun invoke(
        descricao: String,
        valorCentavos: Long,
        categoria: ECategoriaGasto,
        data: Long,
        tipo: ETipoCompraCartao,
        totalParcelas: Int = 1,
        recorrenciaId: Long? = null,
        mesReferenciaCobranca: String? = null,
    ): Long {
        require(descricao.isNotBlank()) { "Descrição obrigatória" }
        require(valorCentavos > 0) { "Valor deve ser maior que zero" }
        require(
            tipo == ETipoCompraCartao.CREDITO_AVISTA ||
                tipo == ETipoCompraCartao.CREDITO_PARCELADO ||
                tipo == ETipoCompraCartao.CREDITO_RECORRENTE ||
                tipo == ETipoCompraCartao.AJUSTE_ESTORNO,
        ) { "Tipo de compra inválido" }

        val parcelasCount = when (tipo) {
            ETipoCompraCartao.CREDITO_PARCELADO -> {
                require(totalParcelas in 2..48) { "Parcelas devem ser entre 2 e 48" }
                totalParcelas
            }
            else -> 1
        }

        val cartao = cartaoRepository.getUnicoOnce()
            ?: error("Configure o cartão de crédito antes de lançar compras")

        if (tipo != ETipoCompraCartao.AJUSTE_ESTORNO) {
            val resumo = cartaoRepository.getResumoLimiteOnce(cartao.id)
            if (resumo.limiteDisponivelCentavos < valorCentavos) {
                error(
                    "Limite insuficiente. Disponível: " +
                        Money.fromCentavos(resumo.limiteDisponivelCentavos),
                )
            }
        }

        return faturaRepository.withTransaction {
            val valoresParcelas = if (tipo == ETipoCompraCartao.AJUSTE_ESTORNO) {
                listOf(-valorCentavos)
            } else {
                Money.dividirParcelas(valorCentavos, parcelasCount)
            }

            val mesPrimeira = DateUtils.mesReferenciaFatura(data, cartao.diaFechamento)
            val parcelas = mutableListOf<ParcelaCartao>()

            valoresParcelas.forEachIndexed { index, valorParcela ->
                val mesFatura = DateUtils.addMonthsToMesAno(mesPrimeira, index)
                val fatura = faturaRepository.obterOuCriarFatura(
                    cartaoId = cartao.id,
                    mesReferencia = mesFatura,
                    dataFechamento = DateUtils.dataFechamentoNoMes(mesFatura, cartao.diaFechamento),
                    dataVencimento = DateUtils.dataVencimentoNoMes(mesFatura, cartao.diaVencimento),
                )
                // Compra após pagamento antecipado: reabre fatura PAGA
                if (fatura.status == EStatusFatura.PAGA && valorParcela > 0) {
                    faturaRepository.atualizarFatura(fatura.copy(status = EStatusFatura.ABERTA))
                }
                parcelas += ParcelaCartao(
                    compraId = 0,
                    faturaId = fatura.id,
                    numeroParcela = index + 1,
                    valorCentavos = valorParcela,
                )
            }

            val compra = CompraCartao(
                cartaoId = cartao.id,
                recorrenciaId = recorrenciaId,
                mesReferenciaCobranca = mesReferenciaCobranca,
                descricao = descricao,
                valorTotalCentavos = if (tipo == ETipoCompraCartao.AJUSTE_ESTORNO) {
                    -valorCentavos
                } else {
                    valorCentavos
                },
                categoria = categoria,
                data = data,
                tipo = tipo,
                totalParcelas = if (parcelasCount > 1) parcelasCount else null,
            )
            faturaRepository.registrarCompraComParcelas(compra, parcelas)
        }
    }
}
