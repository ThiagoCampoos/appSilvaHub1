package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.ResumoLimite
import kotlinx.coroutines.flow.Flow

interface CartaoRepository {
    suspend fun salvar(cartao: Cartao): Long
    suspend fun atualizar(cartao: Cartao)
    fun getUnico(): Flow<Cartao?>
    suspend fun getUnicoOnce(): Cartao?
    fun getResumoLimite(cartaoId: Long): Flow<ResumoLimite>
    suspend fun getResumoLimiteOnce(cartaoId: Long): ResumoLimite
}
