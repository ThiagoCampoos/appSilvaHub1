package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.SalarioDao
import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.repository.SalarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SalarioRepositoryImpl(
    private val salarioDao: SalarioDao,
) : SalarioRepository {

    override suspend fun salvarSalario(salario: Salario): Long {
        return salarioDao.inserir(salario.toEntity())
    }

    override suspend fun atualizarSalario(salario: Salario) {
        salarioDao.atualizar(salario.toEntity())
    }

    override suspend fun deletarSalario(salario: Salario) {
        salarioDao.deletar(salario.toEntity())
    }

    override suspend fun deletarSalarioPorId(id: Long) {
        salarioDao.deletarPorId(id)
    }

    override fun getSalarioDoMes(mesAno: String): Flow<Salario?> {
        return salarioDao.getSalarioDoMes(mesAno).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getTodosSalarios(): Flow<List<Salario>> {
        return salarioDao.getTodosSalarios().map { salarios ->
            salarios.map { it.toDomain() }
        }
    }

    override fun getUltimoSalario(): Flow<Salario?> {
        return salarioDao.getUltimoSalario().map { entity ->
            entity?.toDomain()
        }
    }
}

