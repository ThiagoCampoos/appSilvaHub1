package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "compras_cartao",
    foreignKeys = [
        ForeignKey(
            entity = CartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartao_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RecorrenciaCartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["recorrencia_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["cartao_id"]),
        Index(value = ["recorrencia_id", "mes_referencia_cobranca"], unique = true),
        Index(value = ["recorrencia_id"]),
    ],
)
data class CompraCartaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "cartao_id")
    val cartaoId: Long,
    @ColumnInfo(name = "recorrencia_id")
    val recorrenciaId: Long? = null,
    @ColumnInfo(name = "mes_referencia_cobranca")
    val mesReferenciaCobranca: String? = null,
    val descricao: String,
    @ColumnInfo(name = "valor_total_centavos")
    val valorTotalCentavos: Long,
    val categoria: String,
    val data: Long,
    val tipo: String,
    @ColumnInfo(name = "total_parcelas")
    val totalParcelas: Int? = null,
    val estornada: Boolean = false,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis(),
)
