package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contas_fixas")
data class ContaFixaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val valor: Double,
    @ColumnInfo(name = "dia_vencimento")
    val diaVencimento: Int,
    val ativa: Boolean = true,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()
)