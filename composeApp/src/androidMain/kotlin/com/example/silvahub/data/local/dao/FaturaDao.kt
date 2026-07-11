package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.silvahub.data.local.entity.CompraCartaoEntity
import com.example.silvahub.data.local.entity.FaturaEntity
import com.example.silvahub.data.local.entity.ParcelaCartaoEntity
import com.example.silvahub.data.local.entity.RecorrenciaCartaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaturaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFatura(fatura: FaturaEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirFaturaIgnore(fatura: FaturaEntity): Long

    @Update
    suspend fun atualizarFatura(fatura: FaturaEntity)

    @Query("SELECT * FROM faturas WHERE id = :id LIMIT 1")
    fun getFaturaPorId(id: Long): Flow<FaturaEntity?>

    @Query("SELECT * FROM faturas WHERE id = :id LIMIT 1")
    suspend fun getFaturaPorIdOnce(id: Long): FaturaEntity?

    @Query(
        "SELECT * FROM faturas WHERE cartao_id = :cartaoId AND mes_referencia = :mesReferencia LIMIT 1",
    )
    suspend fun getFaturaPorMes(cartaoId: Long, mesReferencia: String): FaturaEntity?

    @Query("SELECT * FROM faturas WHERE cartao_id = :cartaoId ORDER BY mes_referencia DESC")
    fun getFaturasDoCartao(cartaoId: Long): Flow<List<FaturaEntity>>

    @Query("SELECT * FROM faturas ORDER BY mes_referencia DESC")
    fun getTodasFaturas(): Flow<List<FaturaEntity>>

    @Query("DELETE FROM faturas")
    suspend fun deletarTodasFaturas()

    // --- Compras ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirCompra(compra: CompraCartaoEntity): Long

    @Update
    suspend fun atualizarCompra(compra: CompraCartaoEntity)

    @Query("SELECT * FROM compras_cartao WHERE id = :id LIMIT 1")
    fun getCompraPorId(id: Long): Flow<CompraCartaoEntity?>

    @Query("SELECT * FROM compras_cartao WHERE id = :id LIMIT 1")
    suspend fun getCompraPorIdOnce(id: Long): CompraCartaoEntity?

    @Query("SELECT * FROM compras_cartao WHERE cartao_id = :cartaoId ORDER BY data DESC")
    fun getComprasDoCartao(cartaoId: Long): Flow<List<CompraCartaoEntity>>

    @Query("SELECT * FROM compras_cartao ORDER BY data DESC")
    fun getTodasCompras(): Flow<List<CompraCartaoEntity>>

    @Query(
        "SELECT * FROM compras_cartao " +
            "WHERE recorrencia_id = :recorrenciaId AND mes_referencia_cobranca = :mesReferencia LIMIT 1",
    )
    suspend fun getCompraRecorrenteDoMes(recorrenciaId: Long, mesReferencia: String): CompraCartaoEntity?

    @Query("DELETE FROM compras_cartao WHERE id = :id")
    suspend fun deletarCompraPorId(id: Long)

    @Query("DELETE FROM compras_cartao")
    suspend fun deletarTodasCompras()

    // --- Parcelas ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirParcela(parcela: ParcelaCartaoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirParcelas(parcelas: List<ParcelaCartaoEntity>): List<Long>

    @Update
    suspend fun atualizarParcela(parcela: ParcelaCartaoEntity)

    @Query("SELECT * FROM parcelas_cartao WHERE id = :id LIMIT 1")
    suspend fun getParcelaPorIdOnce(id: Long): ParcelaCartaoEntity?

    @Query("SELECT * FROM parcelas_cartao WHERE compra_id = :compraId ORDER BY numero_parcela ASC")
    suspend fun getParcelasDaCompra(compraId: Long): List<ParcelaCartaoEntity>

    @Query("SELECT * FROM parcelas_cartao WHERE compra_id = :compraId ORDER BY numero_parcela ASC")
    fun getParcelasDaCompraFlow(compraId: Long): Flow<List<ParcelaCartaoEntity>>

    @Query("SELECT * FROM parcelas_cartao WHERE fatura_id = :faturaId ORDER BY id ASC")
    fun getParcelasDaFatura(faturaId: Long): Flow<List<ParcelaCartaoEntity>>

    @Query("SELECT * FROM parcelas_cartao WHERE fatura_id = :faturaId ORDER BY id ASC")
    suspend fun getParcelasDaFaturaOnce(faturaId: Long): List<ParcelaCartaoEntity>

    @Query("SELECT * FROM parcelas_cartao ORDER BY id ASC")
    fun getTodasParcelas(): Flow<List<ParcelaCartaoEntity>>

    @Query("SELECT * FROM parcelas_cartao ORDER BY id ASC")
    suspend fun getTodasParcelasOnce(): List<ParcelaCartaoEntity>

    @Query(
        "SELECT COALESCE(SUM(valor_centavos), 0) FROM parcelas_cartao WHERE fatura_id = :faturaId",
    )
    fun somaParcelasDaFatura(faturaId: Long): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(valor_centavos), 0) FROM parcelas_cartao WHERE fatura_id = :faturaId",
    )
    suspend fun somaParcelasDaFaturaOnce(faturaId: Long): Long

    @Query(
        "SELECT COALESCE(SUM(p.valor_centavos), 0) FROM parcelas_cartao p " +
            "INNER JOIN compras_cartao c ON c.id = p.compra_id " +
            "WHERE c.cartao_id = :cartaoId",
    )
    fun somaTodasParcelasDoCartao(cartaoId: Long): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(p.valor_centavos), 0) FROM parcelas_cartao p " +
            "INNER JOIN compras_cartao c ON c.id = p.compra_id " +
            "WHERE c.cartao_id = :cartaoId",
    )
    suspend fun somaTodasParcelasDoCartaoOnce(cartaoId: Long): Long

    @Query("DELETE FROM parcelas_cartao WHERE compra_id = :compraId")
    suspend fun deletarParcelasDaCompra(compraId: Long)

    @Query("DELETE FROM parcelas_cartao WHERE id = :id")
    suspend fun deletarParcelaPorId(id: Long)

    @Query("DELETE FROM parcelas_cartao")
    suspend fun deletarTodasParcelas()

    // --- Recorrências ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRecorrencia(recorrencia: RecorrenciaCartaoEntity): Long

    @Update
    suspend fun atualizarRecorrencia(recorrencia: RecorrenciaCartaoEntity)

    @Query("SELECT * FROM recorrencias_cartao WHERE id = :id LIMIT 1")
    suspend fun getRecorrenciaPorIdOnce(id: Long): RecorrenciaCartaoEntity?

    @Query("SELECT * FROM recorrencias_cartao WHERE cartao_id = :cartaoId AND ativa = 1")
    fun getRecorrenciasAtivas(cartaoId: Long): Flow<List<RecorrenciaCartaoEntity>>

    @Query("SELECT * FROM recorrencias_cartao WHERE cartao_id = :cartaoId AND ativa = 1")
    suspend fun getRecorrenciasAtivasOnce(cartaoId: Long): List<RecorrenciaCartaoEntity>

    @Query("SELECT * FROM recorrencias_cartao ORDER BY id DESC")
    fun getTodasRecorrencias(): Flow<List<RecorrenciaCartaoEntity>>

    @Query("DELETE FROM recorrencias_cartao")
    suspend fun deletarTodasRecorrencias()

    /**
     * Insere compra + parcelas atomicamente.
     * As faturas devem já existir (ou serem criadas antes nesta mesma transação).
     */
    @Transaction
    suspend fun registrarCompraComParcelas(
        compra: CompraCartaoEntity,
        parcelasSemCompraId: List<ParcelaCartaoEntity>,
    ): Long {
        val compraId = inserirCompra(compra)
        val parcelas = parcelasSemCompraId.map { it.copy(compraId = compraId) }
        inserirParcelas(parcelas)
        return compraId
    }

    @Transaction
    suspend fun obterOuCriarFatura(
        cartaoId: Long,
        mesReferencia: String,
        dataFechamento: Long,
        dataVencimento: Long,
    ): FaturaEntity {
        val existente = getFaturaPorMes(cartaoId, mesReferencia)
        if (existente != null) return existente
        val id = inserirFaturaIgnore(
            FaturaEntity(
                cartaoId = cartaoId,
                mesReferencia = mesReferencia,
                dataFechamento = dataFechamento,
                dataVencimento = dataVencimento,
            ),
        )
        if (id == -1L) {
            return getFaturaPorMes(cartaoId, mesReferencia)
                ?: error("Falha ao obter fatura $mesReferencia")
        }
        return getFaturaPorIdOnce(id) ?: error("Fatura $id não encontrada após insert")
    }
}
