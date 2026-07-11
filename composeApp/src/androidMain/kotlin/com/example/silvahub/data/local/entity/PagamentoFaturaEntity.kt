package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagamentos_fatura",
    foreignKeys = [
        ForeignKey(
            entity = FaturaEntity::class,
            parentColumns = ["id"],
            childColumns = ["fatura_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["fatura_id"])],
)
data class PagamentoFaturaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "fatura_id")
    val faturaId: Long,
    @ColumnInfo(name = "valor_centavos")
    val valorCentavos: Long,
    val data: Long,
    val estornado: Boolean = false,
    @ColumnInfo(name = "data_estorno")
    val dataEstorno: Long? = null,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis(),
)
