package com.example.silvahub.domain.model

import com.example.silvahub.data.local.entity.ECategoriaGasto
import com.example.silvahub.data.local.entity.ETipoGasto

data class Gasto (
    val id: String = java.util.UUID.randomUUID().toString(),
    val usuarioId: String = "",
    val descricao: String,
    val valor: Double,
    val categoria: ECategoriaGasto,
    val data : Long,
    val tipo : ETipoGasto = ETipoGasto.RAPIDO,
    val dataCriacao: Long = System.currentTimeMillis()
    )

