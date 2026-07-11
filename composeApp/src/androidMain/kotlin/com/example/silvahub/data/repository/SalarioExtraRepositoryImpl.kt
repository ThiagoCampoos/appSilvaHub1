package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.SalarioExtraDao
import com.example.silvahub.domain.model.SalarioExtra
import com.example.silvahub.domain.repository.SalarioExtraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SalarioExtraRepositoryImpl(
    private val dao: SalarioExtraDao,
) : SalarioExtraRepository {
    override suspend fun salvar(extra: SalarioExtra): Long = dao.inserir(extra.toEntity())

    override suspend fun deletarPorId(id: Long) = dao.deletarPorId(id)

    override fun getPorMes(mesAno: String): Flow<List<SalarioExtra>> {
        return dao.getPorMes(mesAno).map { list -> list.map { it.toDomain() } }
    }

    override fun getTotalPorMes(mesAno: String): Flow<Double> = dao.getTotalPorMes(mesAno)
}
