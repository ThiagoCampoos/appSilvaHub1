package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parcelas_cartao",
    foreignKeys = [
        ForeignKey(
            entity = CompraCartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["compra_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FaturaEntity::class,
            parentColumns = ["id"],
            childColumns = ["fatura_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["compra_id"]),
        Index(value = ["fatura_id"]),
    ],
)
data class ParcelaCartaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "compra_id")
    val compraId: Long,
    @ColumnInfo(name = "fatura_id")
    val faturaId: Long,
    @ColumnInfo(name = "numero_parcela")
    val numeroParcela: Int,
    @ColumnInfo(name = "valor_centavos")
    val valorCentavos: Long,
)
