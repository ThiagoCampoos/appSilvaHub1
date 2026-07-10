package com.example.silvahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salarios_extras")
data class SalarioExtraEntity(
    @PrimaryKey
    val id: String,
    val usuarioId: String,
    val descricao: String,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long = System.currentTimeMillis(),
)
