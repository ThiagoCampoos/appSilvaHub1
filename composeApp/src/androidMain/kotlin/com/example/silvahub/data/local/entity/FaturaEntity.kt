package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "faturas",
    foreignKeys = [
        ForeignKey(
            entity = CartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartao_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["cartao_id", "mes_referencia"], unique = true),
        Index(value = ["cartao_id"]),
    ],
)
data class FaturaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "cartao_id")
    val cartaoId: Long,
    @ColumnInfo(name = "mes_referencia")
    val mesReferencia: String,
    @ColumnInfo(name = "data_fechamento")
    val dataFechamento: Long,
    @ColumnInfo(name = "data_vencimento")
    val dataVencimento: Long,
    @ColumnInfo(name = "valor_pago_centavos")
    val valorPagoCentavos: Long = 0,
    val status: String = "ABERTA",
)
