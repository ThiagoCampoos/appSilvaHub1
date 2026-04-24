package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.Salario
import kotlinx.coroutines.flow.Flow

interface SalarioRepository {
    suspend fun salvarSalario(salario: Salario): Long
    suspend fun atualizarSalario(salario: Salario)
    suspend fun deletarSalario(salario: Salario)
    suspend fun deletarSalarioPorId(id: Long)

    fun getSalarioDoMes(mesAno: String): Flow<Salario?>
    fun getTodosSalarios(): Flow<List<Salario>>
    fun getUltimoSalario(): Flow<Salario?>
}

