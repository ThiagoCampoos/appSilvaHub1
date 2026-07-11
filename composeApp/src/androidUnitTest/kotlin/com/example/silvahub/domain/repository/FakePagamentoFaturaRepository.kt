package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.PagamentoFatura
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePagamentoFaturaRepository(
    private val somaPeriodo: Long = 0L,
) : PagamentoFaturaRepository {
    override suspend fun salvar(pagamento: PagamentoFatura) = 1L
    override suspend fun atualizar(pagamento: PagamentoFatura) = Unit
    override suspend fun getPorIdOnce(id: Long): PagamentoFatura? = null
    override fun getPorFatura(faturaId: Long) = flowOf(emptyList<PagamentoFatura>())
    override suspend fun getPorFaturaOnce(faturaId: Long) = emptyList<PagamentoFatura>()
    override fun somaPagamentosNoPeriodo(dataInicial: Long, dataFinal: Long): Flow<Long> =
        flowOf(somaPeriodo)
    override fun somaPagamentosAtivosDoCartao(cartaoId: Long): Flow<Long> = flowOf(0L)
    override suspend fun somaPagamentosAtivosDoCartaoOnce(cartaoId: Long) = 0L
}
