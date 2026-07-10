package com.example.silvahub.domain.model

data class ContaFixa(
    val id: String = java.util.UUID.randomUUID().toString(),
    val usuarioId: String = "",
    val nome: String,
    val valor: Double,
    val diaVencimento: Int,
    val ativa: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis()
)
