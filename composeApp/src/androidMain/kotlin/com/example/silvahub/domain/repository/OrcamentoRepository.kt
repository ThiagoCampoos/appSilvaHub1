package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Orcamento
import kotlinx.coroutines.flow.Flow

interface OrcamentoRepository {
    suspend fun salvar(orcamento: Orcamento): Long
    suspend fun atualizar(orcamento: Orcamento)
    suspend fun deletarPorId(id: Long)
    fun getAtivos(): Flow<List<Orcamento>>
    fun getTodos(): Flow<List<Orcamento>>
    fun getPorCategoria(categoria: ECategoriaGasto): Flow<Orcamento?>
}
