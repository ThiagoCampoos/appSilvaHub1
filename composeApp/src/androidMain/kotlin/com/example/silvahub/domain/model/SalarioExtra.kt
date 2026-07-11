package com.example.silvahub.domain.model

data class SalarioExtra(
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long = System.currentTimeMillis(),
)
