package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salarios")
data class SalarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val valor: Double,
    @ColumnInfo(name = "mes_referencia")
    val mesReferencia: String,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()
)
