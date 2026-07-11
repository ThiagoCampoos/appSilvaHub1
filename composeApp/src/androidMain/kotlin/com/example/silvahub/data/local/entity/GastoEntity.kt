package com.example.silvahub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val categoria: ECategoriaGasto,
    val data: Long,
    val tipo: ETipoGasto = ETipoGasto.RAPIDO,
    @ColumnInfo(name = "parcela_atual")
    val parcelaAtual: Int? = null,
    @ColumnInfo(name = "total_parcelas")
    val totalParcelas: Int? = null,
    @ColumnInfo(name = "grupo_parcelamento_id")
    val grupoParcelamentoId: String? = null,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis(),
)
