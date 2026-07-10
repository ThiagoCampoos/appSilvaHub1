package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.ContaFixa
import kotlinx.coroutines.flow.Flow

interface ContaFixaRepository {
    suspend fun salvarConta(conta: ContaFixa)
    suspend fun atualizarConta(conta: ContaFixa)
    suspend fun deletarConta(conta: ContaFixa)
    suspend fun deletarContaPorId(
        id: String
    )

    fun getContasFixasAtivas(): Flow<List<ContaFixa>>
    fun getTotalContasFixasAtivas(): Flow<Double>
    fun getTodasAsContas(): Flow<List<ContaFixa>>
    fun getContaPorId(id: String): Flow<ContaFixa?>
}

