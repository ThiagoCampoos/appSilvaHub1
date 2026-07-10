package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.SalarioExtraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalarioExtraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(extra: SalarioExtraEntity): Long

    @Update
    suspend fun atualizar(extra: SalarioExtraEntity)

    @Delete
    suspend fun deletar(extra: SalarioExtraEntity)

    @Query("DELETE FROM salarios_extras WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("SELECT * FROM salarios_extras WHERE mes_referencia = :mesAno ORDER BY data_criacao DESC")
    fun getPorMes(mesAno: String): Flow<List<SalarioExtraEntity>>

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM salarios_extras WHERE mes_referencia = :mesAno")
    fun getTotalPorMes(mesAno: String): Flow<Double>

    @Query("SELECT * FROM salarios_extras ORDER BY data_criacao DESC")
    fun getTodos(): Flow<List<SalarioExtraEntity>>
}
