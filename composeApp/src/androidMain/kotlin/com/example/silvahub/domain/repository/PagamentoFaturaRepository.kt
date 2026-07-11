package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.PagamentoFatura
import kotlinx.coroutines.flow.Flow

interface PagamentoFaturaRepository {
    suspend fun salvar(pagamento: PagamentoFatura): Long
    suspend fun atualizar(pagamento: PagamentoFatura)
    suspend fun getPorIdOnce(id: Long): PagamentoFatura?
    fun getPorFatura(faturaId: Long): Flow<List<PagamentoFatura>>
    suspend fun getPorFaturaOnce(faturaId: Long): List<PagamentoFatura>
    fun somaPagamentosNoPeriodo(dataInicial: Long, dataFinal: Long): Flow<Long>
    fun somaPagamentosAtivosDoCartao(cartaoId: Long): Flow<Long>
    suspend fun somaPagamentosAtivosDoCartaoOnce(cartaoId: Long): Long
}
