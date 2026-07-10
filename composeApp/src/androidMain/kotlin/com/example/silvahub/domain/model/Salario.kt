package com.example.silvahub.domain.model

data class Salario(
    val id: String = java.util.UUID.randomUUID().toString(),
    val usuarioId: String = "",
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long = System.currentTimeMillis(),
)

