package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.SalarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalarioDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(salario: SalarioEntity) : Long

    @Update
    suspend fun atualizar(salario: SalarioEntity)

    @Delete
    suspend fun deletar(salario: SalarioEntity)

    @Query("DELETE FROM salarios WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("SELECT * FROM salarios WHERE mes_referencia = :mesAno LIMIT 1")
    fun getSalarioDoMes(mesAno: String): Flow<SalarioEntity?>

    @Query("SELECT * FROM salarios ORDER BY data_criacao DESC")
    fun getTodosSalarios(): Flow<List<SalarioEntity>>

    @Query("SELECT * FROM salarios ORDER BY data_criacao DESC LIMIT 1")
    fun getUltimoSalario(): Flow<SalarioEntity?>
}
