package com.example.silvahub.domain.repository

import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.Fatura
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.model.RecorrenciaCartao
import kotlinx.coroutines.flow.Flow

interface FaturaRepository {
    fun getFaturas(cartaoId: Long): Flow<List<Fatura>>
    fun getFaturaPorId(id: Long): Flow<Fatura?>
    suspend fun getFaturaPorIdOnce(id: Long): Fatura?
    suspend fun getFaturaPorMes(cartaoId: Long, mesReferencia: String): Fatura?
    suspend fun obterOuCriarFatura(
        cartaoId: Long,
        mesReferencia: String,
        dataFechamento: Long,
        dataVencimento: Long,
    ): Fatura

    suspend fun atualizarFatura(fatura: Fatura)
    fun somaParcelasDaFatura(faturaId: Long): Flow<Long>
    suspend fun somaParcelasDaFaturaOnce(faturaId: Long): Long

    // Compras
    suspend fun registrarCompraComParcelas(
        compra: CompraCartao,
        parcelas: List<ParcelaCartao>,
    ): Long

    fun getCompraPorId(id: Long): Flow<CompraCartao?>
    suspend fun getCompraPorIdOnce(id: Long): CompraCartao?
    fun getComprasDoCartao(cartaoId: Long): Flow<List<CompraCartao>>
    suspend fun atualizarCompra(compra: CompraCartao)
    suspend fun deletarCompraComParcelas(compraId: Long)
    suspend fun getCompraRecorrenteDoMes(recorrenciaId: Long, mesReferencia: String): CompraCartao?

    // Parcelas
    suspend fun getParcelasDaCompra(compraId: Long): List<ParcelaCartao>
    fun getParcelasDaCompraFlow(compraId: Long): Flow<List<ParcelaCartao>>
    fun getParcelasDaFatura(faturaId: Long): Flow<List<ParcelaCartao>>
    suspend fun getParcelasDaFaturaOnce(faturaId: Long): List<ParcelaCartao>
    suspend fun atualizarParcela(parcela: ParcelaCartao)
    suspend fun deletarParcelasDaCompra(compraId: Long)
    suspend fun inserirParcelas(parcelas: List<ParcelaCartao>): List<Long>

    // Recorrências
    suspend fun salvarRecorrencia(recorrencia: RecorrenciaCartao): Long
    suspend fun atualizarRecorrencia(recorrencia: RecorrenciaCartao)
    suspend fun getRecorrenciaPorIdOnce(id: Long): RecorrenciaCartao?
    fun getRecorrenciasAtivas(cartaoId: Long): Flow<List<RecorrenciaCartao>>
    suspend fun getRecorrenciasAtivasOnce(cartaoId: Long): List<RecorrenciaCartao>
    fun getTodasRecorrencias(): Flow<List<RecorrenciaCartao>>

    /** Executa bloco dentro de transação Room. */
    suspend fun <T> withTransaction(block: suspend () -> T): T
}
