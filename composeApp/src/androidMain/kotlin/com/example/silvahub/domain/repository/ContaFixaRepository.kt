package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.ContaFixa
import kotlinx.coroutines.flow.Flow

interface ContaFixaRepository {
    suspend fun salvarConta(conta: ContaFixa): Long
    suspend fun atualizarConta(conta: ContaFixa)
    suspend fun deletarConta(conta: ContaFixa)
    suspend fun deletarContaPorId(id: Long)

    fun getContasFixasAtivas(): Flow<List<ContaFixa>>
    fun getTotalContasFixasAtivas(): Flow<Double>
    fun getTodasAsContas(): Flow<List<ContaFixa>>
    fun getContaPorId(id: Long): Flow<ContaFixa?>
}

