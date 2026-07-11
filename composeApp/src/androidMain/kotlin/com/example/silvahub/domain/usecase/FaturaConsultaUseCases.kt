package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.FaturaDetalhe
import com.example.silvahub.domain.model.ParcelaCartaoComCompra
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.repository.PagamentoFaturaRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class ObterFaturaAtualUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    operator fun invoke(): Flow<FaturaDetalhe?> {
        return cartaoRepository.getUnico().flatMapLatest { cartao ->
            if (cartao == null) {
                flowOf(null)
            } else {
                val mesFatura = DateUtils.mesReferenciaFatura(
                    System.currentTimeMillis(),
                    cartao.diaFechamento,
                )
                faturaRepository.getFaturas(cartao.id).flatMapLatest { faturas ->
                    val fatura = faturas.find { it.mesReferencia == mesFatura }
                        ?: faturas.firstOrNull { it.status == EStatusFatura.ABERTA }
                    if (fatura == null) {
                        flowOf(null)
                    } else {
                        faturaRepository.somaParcelasDaFatura(fatura.id).map { total ->
                            FaturaDetalhe(
                                fatura = fatura,
                                valorTotalCentavos = total,
                                saldoPendenteCentavos = (total - fatura.valorPagoCentavos)
                                    .coerceAtLeast(0),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObterFaturasUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
) {
    operator fun invoke(): Flow<List<FaturaDetalhe>> {
        return cartaoRepository.getUnico().flatMapLatest { cartao ->
            if (cartao == null) {
                flowOf(emptyList())
            } else {
                faturaRepository.getFaturas(cartao.id).flatMapLatest { faturas ->
                    if (faturas.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            faturas.map { f ->
                                faturaRepository.somaParcelasDaFatura(f.id).map { total ->
                                    FaturaDetalhe(
                                        fatura = f,
                                        valorTotalCentavos = total,
                                        saldoPendenteCentavos = (total - f.valorPagoCentavos)
                                            .coerceAtLeast(0),
                                    )
                                }
                            },
                        ) { detalhes -> detalhes.toList() }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObterDetalhesFaturaUseCase(
    private val faturaRepository: FaturaRepository,
    private val pagamentoFaturaRepository: PagamentoFaturaRepository,
) {
    operator fun invoke(faturaId: Long): Flow<FaturaDetalhe?> {
        return faturaRepository.getFaturaPorId(faturaId).flatMapLatest { fatura ->
            if (fatura == null) {
                flowOf(null)
            } else {
                combine(
                    faturaRepository.somaParcelasDaFatura(faturaId),
                    faturaRepository.getParcelasDaFatura(faturaId),
                    pagamentoFaturaRepository.getPorFatura(faturaId),
                ) { total, _, pagamentos ->
                    Triple(total, pagamentos, fatura)
                }.flatMapLatest { (total, pagamentos, f) ->
                    flow {
                        val parcelas = faturaRepository.getParcelasDaFaturaOnce(faturaId)
                        val comCompra = parcelas.mapNotNull { parcela ->
                            val compra = faturaRepository.getCompraPorIdOnce(parcela.compraId)
                                ?: return@mapNotNull null
                            ParcelaCartaoComCompra(parcela, compra)
                        }
                        emit(
                            FaturaDetalhe(
                                fatura = f,
                                valorTotalCentavos = total,
                                saldoPendenteCentavos = (total - f.valorPagoCentavos)
                                    .coerceAtLeast(0),
                                parcelas = comCompra,
                                pagamentos = pagamentos,
                            ),
                        )
                    }
                }
            }
        }
    }
}
