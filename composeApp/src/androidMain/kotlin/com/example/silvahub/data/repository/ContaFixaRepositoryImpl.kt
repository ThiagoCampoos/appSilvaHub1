package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.ContaFixaDao
import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.repository.ContaFixaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContaFixaRepositoryImpl(
    private val contaFixaDao: ContaFixaDao,
) : ContaFixaRepository {

    override suspend fun salvarConta(conta: ContaFixa): Long {
        return contaFixaDao.inserir(conta.toEntity())
    }

    override suspend fun atualizarConta(conta: ContaFixa) {
        contaFixaDao.atualizar(conta.toEntity())
    }

    override suspend fun deletarConta(conta: ContaFixa) {
        contaFixaDao.deletar(conta.toEntity())
    }

    override suspend fun deletarContaPorId(id: Long) {
        contaFixaDao.deletarPorId(id)
    }

    override fun getContasFixasAtivas(): Flow<List<ContaFixa>> {
        return contaFixaDao.getContasFixasAtivas().map { contas ->
            contas.map { it.toDomain() }
        }
    }

    override fun getTotalContasFixasAtivas(): Flow<Double> {
        return contaFixaDao.getTotalContasFixasAtivas()
    }

    override fun getTodasAsContas(): Flow<List<ContaFixa>> {
        return contaFixaDao.getTodasAsContas().map { contas ->
            contas.map { it.toDomain() }
        }
    }

    override fun getContaPorId(id: Long): Flow<ContaFixa?> {
        return contaFixaDao.getContaPorId(id).map { entity ->
            entity?.toDomain()
        }
    }
}

