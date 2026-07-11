package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.silvahub.domain.model.Cartao

@Entity(tableName = "cartoes")
data class CartaoEntity(
    @PrimaryKey
    val id: Long = Cartao.CARTAO_UNICO_ID,
    @ColumnInfo(name = "limite_centavos")
    val limiteCentavos: Long,
    @ColumnInfo(name = "dia_fechamento")
    val diaFechamento: Int,
    @ColumnInfo(name = "dia_vencimento")
    val diaVencimento: Int,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis(),
)
