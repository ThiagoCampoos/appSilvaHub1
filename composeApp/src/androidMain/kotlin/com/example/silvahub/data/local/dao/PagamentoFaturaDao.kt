package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.silvahub.data.local.entity.FaturaEntity
import com.example.silvahub.data.local.entity.PagamentoFaturaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PagamentoFaturaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(pagamento: PagamentoFaturaEntity): Long

    @Update
    suspend fun atualizar(pagamento: PagamentoFaturaEntity)

    @Query("SELECT * FROM pagamentos_fatura WHERE id = :id LIMIT 1")
    suspend fun getPorIdOnce(id: Long): PagamentoFaturaEntity?

    @Query(
        "SELECT * FROM pagamentos_fatura WHERE fatura_id = :faturaId ORDER BY data DESC",
    )
    fun getPorFatura(faturaId: Long): Flow<List<PagamentoFaturaEntity>>

    @Query(
        "SELECT * FROM pagamentos_fatura WHERE fatura_id = :faturaId ORDER BY data DESC",
    )
    suspend fun getPorFaturaOnce(faturaId: Long): List<PagamentoFaturaEntity>

    @Query(
        "SELECT COALESCE(SUM(valor_centavos), 0) FROM pagamentos_fatura " +
            "WHERE fatura_id = :faturaId AND estornado = 0",
    )
    suspend fun somaPagamentosAtivosDaFatura(faturaId: Long): Long

    @Query(
        "SELECT COALESCE(SUM(p.valor_centavos), 0) FROM pagamentos_fatura p " +
            "INNER JOIN faturas f ON f.id = p.fatura_id " +
            "WHERE f.cartao_id = :cartaoId AND p.estornado = 0",
    )
    fun somaPagamentosAtivosDoCartao(cartaoId: Long): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(p.valor_centavos), 0) FROM pagamentos_fatura p " +
            "INNER JOIN faturas f ON f.id = p.fatura_id " +
            "WHERE f.cartao_id = :cartaoId AND p.estornado = 0",
    )
    suspend fun somaPagamentosAtivosDoCartaoOnce(cartaoId: Long): Long

    @Query(
        "SELECT COALESCE(SUM(valor_centavos), 0) FROM pagamentos_fatura " +
            "WHERE estornado = 0 AND data BETWEEN :dataInicial AND :dataFinal",
    )
    fun somaPagamentosNoPeriodo(dataInicial: Long, dataFinal: Long): Flow<Long>

    @Query("SELECT * FROM pagamentos_fatura ORDER BY data DESC")
    fun getTodos(): Flow<List<PagamentoFaturaEntity>>

    @Query("DELETE FROM pagamentos_fatura")
    suspend fun deletarTodos()

    @Transaction
    suspend fun registrarPagamentoEAtualizarFatura(
        pagamento: PagamentoFaturaEntity,
        faturaAtualizada: FaturaEntity,
    ): Long {
        val id = inserir(pagamento)
        // FaturaDao.atualizarFatura precisa ser chamado via repository com withTransaction
        return id
    }
}
