package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.domain.model.ECategoriaGasto
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(gasto: GastoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(gastos: List<GastoEntity>): List<Long>

    @Update
    suspend fun atualizar(gasto: GastoEntity)

    @Delete
    suspend fun deletar(gasto: GastoEntity)

    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("DELETE FROM gastos WHERE grupo_parcelamento_id = :grupoId AND data >= :dataMinima")
    suspend fun deletarParcelasRestantes(grupoId: String, dataMinima: Long)

    @Query("SELECT * FROM gastos WHERE data BETWEEN :dataInicial AND :dataFinal ORDER BY data DESC")
    fun getGastosPorPeriodo(dataInicial: Long, dataFinal: Long): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE categoria = :categoria ORDER BY data DESC")
    fun getGastosPorCategoria(categoria: ECategoriaGasto): Flow<List<GastoEntity>>

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM gastos WHERE data BETWEEN :dataInicial AND :dataFinal")
    fun gastoTotalPorPeriodo(dataInicial: Long, dataFinal: Long): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(valor), 0.0) FROM gastos " +
            "WHERE categoria = :categoria AND data BETWEEN :dataInicial AND :dataFinal",
    )
    fun gastoTotalPorCategoriaNoPeriodo(
        categoria: ECategoriaGasto,
        dataInicial: Long,
        dataFinal: Long,
    ): Flow<Double>

    @Query("SELECT * FROM gastos ORDER BY data DESC LIMIT :limit")
    fun getUltimosGastos(limit: Int = 10): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos ORDER BY data DESC")
    fun getTodosGastos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun getGastoPorId(id: Long): Flow<GastoEntity?>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    suspend fun getGastoPorIdOnce(id: Long): GastoEntity?
}
