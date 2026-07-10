package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.ECategoriaGasto
import com.example.silvahub.data.local.entity.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(gasto: GastoEntity)

    @Update
    suspend fun atualizar(gasto: GastoEntity)

    @Delete
    suspend fun deletar(gasto: GastoEntity)

    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun deletarPorId(id: String)

    @Query("SELECT * FROM gastos WHERE data BETWEEN :dataInicial AND :dataFinal ORDER BY data DESC")
    fun getGastosPorPeriodo(dataInicial: Long, dataFinal: Long): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE categoria = :categoria ORDER BY data DESC")
    fun getGastosPorCategoria(categoria: ECategoriaGasto): Flow<List<GastoEntity>>

    @Query("SELECT COALESCE(SUM(valor),0.0) FROM gastos WHERE data BETWEEN :dataInicial AND :dataFinal")
    fun gastoTotalPorPeriodo(dataInicial: Long, dataFinal: Long): Flow<Double>


    @Query("SELECT COALESCE(SUM(valor),0.0) FROM gastos WHERE categoria = :categoria")
    fun gastoTotalPorCategoria(categoria: ECategoriaGasto): Flow<Double>


    @Query("SELECT * FROM gastos ORDER BY data DESC LIMIT :limit")
    fun getUltimosGastos(limit: Int = 10): Flow<List<GastoEntity>>
}
