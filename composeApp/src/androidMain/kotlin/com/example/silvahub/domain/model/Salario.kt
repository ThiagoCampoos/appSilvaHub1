package com.example.silvahub.domain.model

data class Salario(
    val id: Long = 0,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long = System.currentTimeMillis(),
)

