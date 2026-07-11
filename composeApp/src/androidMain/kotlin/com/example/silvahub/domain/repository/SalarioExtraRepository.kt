package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.SalarioExtra
import kotlinx.coroutines.flow.Flow

interface SalarioExtraRepository {
    suspend fun salvar(extra: SalarioExtra): Long
    suspend fun deletarPorId(id: Long)
    fun getPorMes(mesAno: String): Flow<List<SalarioExtra>>
    fun getTotalPorMes(mesAno: String): Flow<Double>
}
