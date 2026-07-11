package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.ETipoLancamento
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import com.example.silvahub.util.Money
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Une gastos em débito e parcelas/compras de cartão do mês.
 *
 * Crédito entra de duas formas (sem duplicar a mesma parcela):
 * 1. Parcelas da fatura com [mesReferencia] == mês exibido
 * 2. Compras cuja data cai no mês e ainda não foram listadas (ex.: compra após o
 *    fechamento, que vai para a fatura do mês seguinte)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObterLancamentosDoMesUseCase(
    private val gastoRepository: GastoRepository,
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    operator fun invoke(mesAno: String = DateUtils.mesReferenciaAtual()): Flow<List<Lancamento>> {
        val (inicio, fim) = DateUtils.mesAnoToRange(mesAno)

        val gastosFlow = gastoRepository.getGastoDoMes(mesAno).map { gastos ->
            gastos.map { gasto ->
                Lancamento(
                    id = "gasto-${gasto.id}",
                    descricao = gasto.descricao,
                    valor = gasto.valor,
                    categoria = gasto.categoria,
                    data = gasto.data,
                    tipoLancamento = ETipoLancamento.DEBITO_AVISTA,
                    parcelaAtual = gasto.parcelaAtual,
                    totalParcelas = gasto.totalParcelas,
                    gastoId = gasto.id,
                    grupoParcelamentoId = gasto.grupoParcelamentoId,
                    tipoGastoLegado = gasto.tipo,
                )
            }
        }

        val creditoFlow = cartaoRepository.getUnico().flatMapLatest { cartao ->
            if (cartao == null) {
                flowOf(emptyList())
            } else {
                combine(
                    faturaRepository.getFaturas(cartao.id),
                    faturaRepository.getComprasDoCartao(cartao.id),
                ) { faturas, compras -> faturas to compras }
                    .flatMapLatest { (faturas, compras) ->
                        flow {
                            val faturaDoMes = faturas.find { it.mesReferencia == mesAno }
                            val lancamentos = mutableListOf<Lancamento>()
                            val parcelasJaIncluidas = mutableSetOf<Long>()

                            if (faturaDoMes != null) {
                                val parcelas = faturaRepository.getParcelasDaFaturaOnce(faturaDoMes.id)
                                for (parcela in parcelas) {
                                    val compra = faturaRepository.getCompraPorIdOnce(parcela.compraId)
                                        ?: continue
                                    toLancamentoCredito(compra, parcela, faturaDoMes.id)?.let {
                                        lancamentos += it
                                        parcelasJaIncluidas += parcela.id
                                    }
                                }
                            }

                            // Compras feitas neste mês calendário que ainda não entraram
                            // (ex.: após o fechamento → fatura do mês seguinte)
                            for (compra in compras) {
                                if (compra.estornada) continue
                                if (compra.tipo == ETipoCompraCartao.AJUSTE_ESTORNO) continue
                                if (compra.data !in inicio..fim) continue

                                val parcelas = faturaRepository.getParcelasDaCompra(compra.id)
                                val pendentes = parcelas.filter { it.id !in parcelasJaIncluidas }
                                if (pendentes.isEmpty()) continue

                                // Mostra a 1ª parcela ainda não listada (à vista = única)
                                val parcela = pendentes.minByOrNull { it.numeroParcela } ?: continue
                                toLancamentoCredito(compra, parcela, parcela.faturaId)?.let {
                                    lancamentos += it
                                    parcelasJaIncluidas += parcela.id
                                }
                            }

                            emit(lancamentos)
                        }
                    }
            }
        }

        return combine(gastosFlow, creditoFlow) { debito, credito ->
            (debito + credito).sortedByDescending { it.data }
        }
    }

    private fun toLancamentoCredito(
        compra: com.example.silvahub.domain.model.CompraCartao,
        parcela: com.example.silvahub.domain.model.ParcelaCartao,
        faturaId: Long,
    ): Lancamento? {
        if (compra.estornada) return null
        val tipoLancamento = when (compra.tipo) {
            ETipoCompraCartao.CREDITO_AVISTA -> ETipoLancamento.CREDITO_AVISTA
            ETipoCompraCartao.CREDITO_PARCELADO -> ETipoLancamento.CREDITO_PARCELADO
            ETipoCompraCartao.CREDITO_RECORRENTE -> ETipoLancamento.CREDITO_RECORRENTE
            ETipoCompraCartao.AJUSTE_ESTORNO -> return null
        }
        return Lancamento(
            id = "parcela-${parcela.id}",
            descricao = compra.descricao,
            valor = Money.fromCentavos(parcela.valorCentavos),
            valorCentavos = parcela.valorCentavos,
            categoria = compra.categoria,
            data = compra.data,
            tipoLancamento = tipoLancamento,
            parcelaAtual = if (compra.totalParcelas != null) parcela.numeroParcela else null,
            totalParcelas = compra.totalParcelas,
            compraCartaoId = compra.id,
            parcelaCartaoId = parcela.id,
            faturaId = faturaId,
        )
    }
}
