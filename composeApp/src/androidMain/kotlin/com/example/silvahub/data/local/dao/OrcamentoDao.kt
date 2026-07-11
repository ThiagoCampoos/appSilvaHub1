package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.OrcamentoEntity
import com.example.silvahub.domain.model.ECategoriaGasto
import kotlinx.coroutines.flow.Flow

@Dao
interface OrcamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(orcamento: OrcamentoEntity): Long

    @Update
    suspend fun atualizar(orcamento: OrcamentoEntity)

    @Delete
    suspend fun deletar(orcamento: OrcamentoEntity)

    @Query("DELETE FROM orcamentos WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("SELECT * FROM orcamentos WHERE ativo = 1 ORDER BY categoria ASC")
    fun getAtivos(): Flow<List<OrcamentoEntity>>

    @Query("SELECT * FROM orcamentos ORDER BY categoria ASC")
    fun getTodos(): Flow<List<OrcamentoEntity>>

    @Query("SELECT * FROM orcamentos WHERE categoria = :categoria LIMIT 1")
    fun getPorCategoria(categoria: ECategoriaGasto): Flow<OrcamentoEntity?>
}
