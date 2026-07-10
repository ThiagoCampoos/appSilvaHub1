package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.silvahub.domain.model.ECategoriaGasto

@Entity(tableName = "orcamentos")
data class OrcamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoria: ECategoriaGasto,
    @ColumnInfo(name = "limite_mensal")
    val limiteMensal: Double,
    val ativo: Boolean = true,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis(),
)
