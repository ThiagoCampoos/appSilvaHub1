package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silvahub.data.local.entity.CartaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(cartao: CartaoEntity): Long

    @Update
    suspend fun atualizar(cartao: CartaoEntity)

    @Query("SELECT * FROM cartoes WHERE id = :id LIMIT 1")
    fun getPorId(id: Long): Flow<CartaoEntity?>

    @Query("SELECT * FROM cartoes WHERE id = :id LIMIT 1")
    suspend fun getPorIdOnce(id: Long): CartaoEntity?

    @Query("SELECT * FROM cartoes LIMIT 1")
    fun getUnico(): Flow<CartaoEntity?>

    @Query("SELECT * FROM cartoes LIMIT 1")
    suspend fun getUnicoOnce(): CartaoEntity?

    @Query("DELETE FROM cartoes")
    suspend fun deletarTodos()
}
