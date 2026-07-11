package com.example.silvahub.data.repository

import androidx.room.withTransaction
import com.example.silvahub.data.local.dao.FaturaDao
import com.example.silvahub.data.local.database.AppDatabase
import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.Fatura
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.model.RecorrenciaCartao
import com.example.silvahub.domain.repository.FaturaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FaturaRepositoryImpl(
    private val db: AppDatabase,
    private val faturaDao: FaturaDao,
) : FaturaRepository {

    override fun getFaturas(cartaoId: Long): Flow<List<Fatura>> {
        return faturaDao.getFaturasDoCartao(cartaoId).map { list -> list.map { it.toDomain() } }
    }

    override fun getFaturaPorId(id: Long): Flow<Fatura?> {
        return faturaDao.getFaturaPorId(id).map { it?.toDomain() }
    }

    override suspend fun getFaturaPorIdOnce(id: Long): Fatura? {
        return faturaDao.getFaturaPorIdOnce(id)?.toDomain()
    }

    override suspend fun getFaturaPorMes(cartaoId: Long, mesReferencia: String): Fatura? {
        return faturaDao.getFaturaPorMes(cartaoId, mesReferencia)?.toDomain()
    }

    override suspend fun obterOuCriarFatura(
        cartaoId: Long,
        mesReferencia: String,
        dataFechamento: Long,
        dataVencimento: Long,
    ): Fatura {
        return faturaDao.obterOuCriarFatura(
            cartaoId,
            mesReferencia,
            dataFechamento,
            dataVencimento,
        ).toDomain()
    }

    override suspend fun atualizarFatura(fatura: Fatura) {
        faturaDao.atualizarFatura(fatura.toEntity())
    }

    override fun somaParcelasDaFatura(faturaId: Long): Flow<Long> {
        return faturaDao.somaParcelasDaFatura(faturaId)
    }

    override suspend fun somaParcelasDaFaturaOnce(faturaId: Long): Long {
        return faturaDao.somaParcelasDaFaturaOnce(faturaId)
    }

    override suspend fun registrarCompraComParcelas(
        compra: CompraCartao,
        parcelas: List<ParcelaCartao>,
    ): Long {
        return faturaDao.registrarCompraComParcelas(
            compra.toEntity(),
            parcelas.map { it.toEntity().copy(compraId = 0) },
        )
    }

    override fun getCompraPorId(id: Long): Flow<CompraCartao?> {
        return faturaDao.getCompraPorId(id).map { it?.toDomain() }
    }

    override suspend fun getCompraPorIdOnce(id: Long): CompraCartao? {
        return faturaDao.getCompraPorIdOnce(id)?.toDomain()
    }

    override fun getComprasDoCartao(cartaoId: Long): Flow<List<CompraCartao>> {
        return faturaDao.getComprasDoCartao(cartaoId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun atualizarCompra(compra: CompraCartao) {
        faturaDao.atualizarCompra(compra.toEntity())
    }

    override suspend fun deletarCompraComParcelas(compraId: Long) {
        db.withTransaction {
            faturaDao.deletarParcelasDaCompra(compraId)
            faturaDao.deletarCompraPorId(compraId)
        }
    }

    override suspend fun getCompraRecorrenteDoMes(
        recorrenciaId: Long,
        mesReferencia: String,
    ): CompraCartao? {
        return faturaDao.getCompraRecorrenteDoMes(recorrenciaId, mesReferencia)?.toDomain()
    }

    override suspend fun getParcelasDaCompra(compraId: Long): List<ParcelaCartao> {
        return faturaDao.getParcelasDaCompra(compraId).map { it.toDomain() }
    }

    override fun getParcelasDaCompraFlow(compraId: Long): Flow<List<ParcelaCartao>> {
        return faturaDao.getParcelasDaCompraFlow(compraId).map { list -> list.map { it.toDomain() } }
    }

    override fun getParcelasDaFatura(faturaId: Long): Flow<List<ParcelaCartao>> {
        return faturaDao.getParcelasDaFatura(faturaId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getParcelasDaFaturaOnce(faturaId: Long): List<ParcelaCartao> {
        return faturaDao.getParcelasDaFaturaOnce(faturaId).map { it.toDomain() }
    }

    override suspend fun atualizarParcela(parcela: ParcelaCartao) {
        faturaDao.atualizarParcela(parcela.toEntity())
    }

    override suspend fun deletarParcelasDaCompra(compraId: Long) {
        faturaDao.deletarParcelasDaCompra(compraId)
    }

    override suspend fun inserirParcelas(parcelas: List<ParcelaCartao>): List<Long> {
        return faturaDao.inserirParcelas(parcelas.map { it.toEntity() })
    }

    override suspend fun salvarRecorrencia(recorrencia: RecorrenciaCartao): Long {
        return faturaDao.inserirRecorrencia(recorrencia.toEntity())
    }

    override suspend fun atualizarRecorrencia(recorrencia: RecorrenciaCartao) {
        faturaDao.atualizarRecorrencia(recorrencia.toEntity())
    }

    override suspend fun getRecorrenciaPorIdOnce(id: Long): RecorrenciaCartao? {
        return faturaDao.getRecorrenciaPorIdOnce(id)?.toDomain()
    }

    override fun getRecorrenciasAtivas(cartaoId: Long): Flow<List<RecorrenciaCartao>> {
        return faturaDao.getRecorrenciasAtivas(cartaoId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecorrenciasAtivasOnce(cartaoId: Long): List<RecorrenciaCartao> {
        return faturaDao.getRecorrenciasAtivasOnce(cartaoId).map { it.toDomain() }
    }

    override fun getTodasRecorrencias(): Flow<List<RecorrenciaCartao>> {
        return faturaDao.getTodasRecorrencias().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        return db.withTransaction { block() }
    }
}
