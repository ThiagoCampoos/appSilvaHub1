package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.util.DateUtils
import com.example.silvahub.util.Money

class EditarCompraCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    suspend operator fun invoke(
        compraId: Long,
        descricao: String,
        categoria: ECategoriaGasto,
        novoValorCentavos: Long? = null,
        novoTotalParcelas: Int? = null,
    ) {
        require(descricao.isNotBlank()) { "Descrição obrigatória" }
        val compra = faturaRepository.getCompraPorIdOnce(compraId)
            ?: error("Compra não encontrada")
        require(!compra.estornada) { "Compra estornada não pode ser editada" }
        require(compra.tipo != ETipoCompraCartao.AJUSTE_ESTORNO) {
            "Ajuste de estorno não pode ser editado"
        }

        val parcelas = faturaRepository.getParcelasDaCompra(compraId)
        val faturasPagas = parcelas.any { parcela ->
            faturaRepository.getFaturaPorIdOnce(parcela.faturaId)?.status == EStatusFatura.PAGA
        }

        if (novoValorCentavos != null || novoTotalParcelas != null) {
            require(!faturasPagas) {
                "Não é possível alterar valor/parcelas: há parcela em fatura paga. Use estorno."
            }
        }

        faturaRepository.withTransaction {
            var atualizada = compra.copy(descricao = descricao, categoria = categoria)

            if (novoValorCentavos != null || novoTotalParcelas != null) {
                val valor = novoValorCentavos ?: compra.valorTotalCentavos
                val nParcelas = when (compra.tipo) {
                    ETipoCompraCartao.CREDITO_PARCELADO ->
                        (novoTotalParcelas ?: compra.totalParcelas ?: 1).also {
                            require(it in 2..48) { "Parcelas devem ser entre 2 e 48" }
                        }
                    else -> 1
                }
                require(valor > 0) { "Valor deve ser maior que zero" }

                val cartao = cartaoRepository.getUnicoOnce()
                    ?: error("Cartão não encontrado")
                val resumo = cartaoRepository.getResumoLimiteOnce(cartao.id)
                val delta = valor - compra.valorTotalCentavos
                if (delta > 0 && resumo.limiteDisponivelCentavos < delta) {
                    error("Limite insuficiente para o novo valor")
                }

                faturaRepository.deletarParcelasDaCompra(compraId)
                val valores = Money.dividirParcelas(valor, nParcelas)
                val mesPrimeira = DateUtils.mesReferenciaFatura(compra.data, cartao.diaFechamento)
                val novasParcelas = valores.mapIndexed { index, v ->
                    val mesFatura = DateUtils.addMonthsToMesAno(mesPrimeira, index)
                    val fatura = faturaRepository.obterOuCriarFatura(
                        cartaoId = cartao.id,
                        mesReferencia = mesFatura,
                        dataFechamento = DateUtils.dataFechamentoNoMes(mesFatura, cartao.diaFechamento),
                        dataVencimento = DateUtils.dataVencimentoNoMes(mesFatura, cartao.diaVencimento),
                    )
                    if (fatura.status == EStatusFatura.PAGA) {
                        faturaRepository.atualizarFatura(fatura.copy(status = EStatusFatura.ABERTA))
                    }
                    ParcelaCartao(
                        compraId = compraId,
                        faturaId = fatura.id,
                        numeroParcela = index + 1,
                        valorCentavos = v,
                    )
                }
                faturaRepository.inserirParcelas(novasParcelas)
                atualizada = atualizada.copy(
                    valorTotalCentavos = valor,
                    totalParcelas = if (nParcelas > 1) nParcelas else null,
                )
            }
            faturaRepository.atualizarCompra(atualizada)
        }
    }
}

class DeletarCompraCartaoUseCase(
    private val faturaRepository: FaturaRepository,
) {
    suspend operator fun invoke(compraId: Long) {
        faturaRepository.getCompraPorIdOnce(compraId)
            ?: error("Compra não encontrada")
        val parcelas = faturaRepository.getParcelasDaCompra(compraId)
        val temFaturaPaga = parcelas.any { parcela ->
            faturaRepository.getFaturaPorIdOnce(parcela.faturaId)?.status == EStatusFatura.PAGA
        }
        require(!temFaturaPaga) {
            "Não é possível excluir: há parcela em fatura paga. Use estorno."
        }
        faturaRepository.deletarCompraComParcelas(compraId)
    }
}

class AnteciparParcelasUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    suspend operator fun invoke(compraId: Long, numerosParcelas: List<Int>) {
        require(numerosParcelas.isNotEmpty()) { "Selecione ao menos uma parcela" }
        val cartao = cartaoRepository.getUnicoOnce()
            ?: error("Cartão não encontrado")
        val compra = faturaRepository.getCompraPorIdOnce(compraId)
            ?: error("Compra não encontrada")
        require(!compra.estornada) { "Compra estornada" }

        faturaRepository.withTransaction {
            val mesAtual = DateUtils.mesReferenciaFatura(
                System.currentTimeMillis(),
                cartao.diaFechamento,
            )
            val faturaAtual = faturaRepository.obterOuCriarFatura(
                cartaoId = cartao.id,
                mesReferencia = mesAtual,
                dataFechamento = DateUtils.dataFechamentoNoMes(mesAtual, cartao.diaFechamento),
                dataVencimento = DateUtils.dataVencimentoNoMes(mesAtual, cartao.diaVencimento),
            )
            if (faturaAtual.status == EStatusFatura.PAGA) {
                faturaRepository.atualizarFatura(faturaAtual.copy(status = EStatusFatura.ABERTA))
            }

            val parcelas = faturaRepository.getParcelasDaCompra(compraId)
            for (num in numerosParcelas) {
                val parcela = parcelas.find { it.numeroParcela == num }
                    ?: error("Parcela $num não encontrada")
                if (parcela.faturaId == faturaAtual.id) continue
                val faturaOrigem = faturaRepository.getFaturaPorIdOnce(parcela.faturaId)
                require(faturaOrigem?.status != EStatusFatura.PAGA) {
                    "Não é possível antecipar parcela de fatura paga"
                }
                faturaRepository.atualizarParcela(parcela.copy(faturaId = faturaAtual.id))
            }
        }
    }
}
