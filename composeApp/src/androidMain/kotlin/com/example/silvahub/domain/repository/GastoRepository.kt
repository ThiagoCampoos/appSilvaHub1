package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Gasto
import kotlinx.coroutines.flow.Flow

interface GastoRepository {
    suspend fun salvarGasto(gasto: Gasto): Long
    suspend fun salvarGastos(gastos: List<Gasto>): List<Long>
    suspend fun atualizarGasto(gasto: Gasto)
    suspend fun deletarGasto(gasto: Gasto)
    suspend fun deletarGastoPorId(id: Long)
    suspend fun deletarParcelasRestantes(grupoId: String, dataMinima: Long)
    suspend fun getGastoPorIdOnce(id: Long): Gasto?

    fun getGastoDoMes(mesAno: String): Flow<List<Gasto>>
    fun getTodosGastos(): Flow<List<Gasto>>
    fun getUltimosGastos(limit: Int = 10): Flow<List<Gasto>>
    fun getGastoPorId(id: Long): Flow<Gasto?>
    fun getTotalDoMes(mesAno: String): Flow<Double>
    fun getTotalPorCategoriaNoMes(categoria: ECategoriaGasto, mesAno: String): Flow<Double>
}
