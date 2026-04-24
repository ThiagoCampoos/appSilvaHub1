package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.ContaFixaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContaFixaDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(conta: ContaFixaEntity) : Long

    @Update
    suspend fun atualizar(conta: ContaFixaEntity)

    @Delete
    suspend fun deletar(conta: ContaFixaEntity)

    @Query("DELETE FROM contas_fixas WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("SELECT * FROM contas_fixas WHERE ativa = 1 ORDER BY dia_vencimento ASC")
    fun getContasFixasAtivas(): Flow<List<ContaFixaEntity>>

    @Query("SELECT * FROM contas_fixas ORDER BY dia_vencimento ASC")
    fun getTodasAsContas(): Flow<List<ContaFixaEntity>>

    @Query("SELECT * FROM contas_fixas WHERE id = :id LIMIT 1")
    fun getContaPorId(id: Long): Flow<ContaFixaEntity?>

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM contas_fixas WHERE ativa = 1")
    fun getTotalContasFixasAtivas(): Flow<Double>
}
