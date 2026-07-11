package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.OrcamentoDao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Orcamento
import com.example.silvahub.domain.repository.OrcamentoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrcamentoRepositoryImpl(
    private val dao: OrcamentoDao,
) : OrcamentoRepository {
    override suspend fun salvar(orcamento: Orcamento): Long = dao.inserir(orcamento.toEntity())

    override suspend fun atualizar(orcamento: Orcamento) = dao.atualizar(orcamento.toEntity())

    override suspend fun deletarPorId(id: Long) = dao.deletarPorId(id)

    override fun getAtivos(): Flow<List<Orcamento>> {
        return dao.getAtivos().map { list -> list.map { it.toDomain() } }
    }

    override fun getTodos(): Flow<List<Orcamento>> {
        return dao.getTodos().map { list -> list.map { it.toDomain() } }
    }

    override fun getPorCategoria(categoria: ECategoriaGasto): Flow<Orcamento?> {
        return dao.getPorCategoria(categoria).map { it?.toDomain() }
    }
}
