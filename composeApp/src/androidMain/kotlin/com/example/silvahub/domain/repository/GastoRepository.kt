package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.Gasto
import kotlinx.coroutines.flow.Flow

interface GastoRepository {
    suspend fun salvarGasto(gasto: Gasto): Long
    suspend fun atualizarGasto(gasto: Gasto)
    suspend fun deletarGasto(gasto: Gasto)
    suspend fun deletarGastoPorId(id: Long)

    fun getGastoDoMes(mesAno: String): Flow<List<Gasto>>
    fun getTodosGastos(): Flow<List<Gasto>>
}
