package com.example.silvahub.domain.model

data class RecorrenciaCartao(
    val id: Long = 0,
    val cartaoId: Long,
    val descricao: String,
    val valorCentavos: Long,
    val categoria: ECategoriaGasto,
    val diaCobranca: Int,
    val ativa: Boolean = true,
    val dataInicio: Long,
    val dataCancelamento: Long? = null,
)
