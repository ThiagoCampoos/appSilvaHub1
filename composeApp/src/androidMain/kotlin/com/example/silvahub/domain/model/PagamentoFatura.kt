package com.example.silvahub.domain.model

data class PagamentoFatura(
    val id: Long = 0,
    val faturaId: Long,
    val valorCentavos: Long,
    val data: Long,
    val estornado: Boolean = false,
    val dataEstorno: Long? = null,
    val dataCriacao: Long = System.currentTimeMillis(),
)
