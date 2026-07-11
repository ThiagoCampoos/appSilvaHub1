package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.PagamentoFaturaDao
import com.example.silvahub.domain.model.PagamentoFatura
import com.example.silvahub.domain.repository.PagamentoFaturaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagamentoFaturaRepositoryImpl(
    private val pagamentoFaturaDao: PagamentoFaturaDao,
) : PagamentoFaturaRepository {

    override suspend fun salvar(pagamento: PagamentoFatura): Long {
        return pagamentoFaturaDao.inserir(pagamento.toEntity())
    }

    override suspend fun atualizar(pagamento: PagamentoFatura) {
        pagamentoFaturaDao.atualizar(pagamento.toEntity())
    }

    override suspend fun getPorIdOnce(id: Long): PagamentoFatura? {
        return pagamentoFaturaDao.getPorIdOnce(id)?.toDomain()
    }

    override fun getPorFatura(faturaId: Long): Flow<List<PagamentoFatura>> {
        return pagamentoFaturaDao.getPorFatura(faturaId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPorFaturaOnce(faturaId: Long): List<PagamentoFatura> {
        return pagamentoFaturaDao.getPorFaturaOnce(faturaId).map { it.toDomain() }
    }

    override fun somaPagamentosNoPeriodo(dataInicial: Long, dataFinal: Long): Flow<Long> {
        return pagamentoFaturaDao.somaPagamentosNoPeriodo(dataInicial, dataFinal)
    }

    override fun somaPagamentosAtivosDoCartao(cartaoId: Long): Flow<Long> {
        return pagamentoFaturaDao.somaPagamentosAtivosDoCartao(cartaoId)
    }

    override suspend fun somaPagamentosAtivosDoCartaoOnce(cartaoId: Long): Long {
        return pagamentoFaturaDao.somaPagamentosAtivosDoCartaoOnce(cartaoId)
    }
}
