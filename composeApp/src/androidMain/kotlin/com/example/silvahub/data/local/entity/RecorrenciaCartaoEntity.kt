package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recorrencias_cartao",
    foreignKeys = [
        ForeignKey(
            entity = CartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartao_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["cartao_id"])],
)
data class RecorrenciaCartaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "cartao_id")
    val cartaoId: Long,
    val descricao: String,
    @ColumnInfo(name = "valor_centavos")
    val valorCentavos: Long,
    val categoria: String,
    @ColumnInfo(name = "dia_cobranca")
    val diaCobranca: Int,
    val ativa: Boolean = true,
    @ColumnInfo(name = "data_inicio")
    val dataInicio: Long,
    @ColumnInfo(name = "data_cancelamento")
    val dataCancelamento: Long? = null,
)
