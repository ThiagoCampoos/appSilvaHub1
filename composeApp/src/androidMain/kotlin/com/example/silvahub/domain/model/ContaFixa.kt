package com.example.silvahub.domain.model

data class ContaFixa(
    val id: Long = 0,
    val nome: String,
    val valor: Double,
    val diaVencimento: Int,
    val ativa: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis()
)
