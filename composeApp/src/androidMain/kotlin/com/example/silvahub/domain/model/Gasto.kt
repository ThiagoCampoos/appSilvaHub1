package com.example.silvahub.domain.model

data class Gasto(
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val categoria: ECategoriaGasto,
    val data: Long,
    val tipo: ETipoGasto = ETipoGasto.RAPIDO,
    val parcelaAtual: Int? = null,
    val totalParcelas: Int? = null,
    val grupoParcelamentoId: String? = null,
    val dataCriacao: Long = System.currentTimeMillis(),
)
