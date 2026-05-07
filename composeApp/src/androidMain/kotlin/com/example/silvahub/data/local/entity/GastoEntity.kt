package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val categoria: ECategoriaGasto,
    val data: Long,
    val tipo: ETipoGasto = ETipoGasto.RAPIDO,

    @ColumnInfo(name = "data_criacao")
    val dataCriacao : Long = System.currentTimeMillis()
)