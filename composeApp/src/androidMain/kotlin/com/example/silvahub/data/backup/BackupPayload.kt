package com.example.silvahub.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val version: Int = 2,
    val createdAt: Long = System.currentTimeMillis(),
    val salarios: List<SalarioBackup> = emptyList(),
    val contasFixas: List<ContaFixaBackup> = emptyList(),
    val gastos: List<GastoBackup> = emptyList(),
    val salariosExtras: List<SalarioExtraBackup> = emptyList(),
    val orcamentos: List<OrcamentoBackup> = emptyList(),
    val cartoes: List<CartaoBackup> = emptyList(),
    val faturas: List<FaturaBackup> = emptyList(),
    val recorrenciasCartao: List<RecorrenciaCartaoBackup> = emptyList(),
    val comprasCartao: List<CompraCartaoBackup> = emptyList(),
    val parcelasCartao: List<ParcelaCartaoBackup> = emptyList(),
    val pagamentosFatura: List<PagamentoFaturaBackup> = emptyList(),
)

@Serializable
data class SalarioBackup(
    val id: Long,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long,
)

@Serializable
data class ContaFixaBackup(
    val id: Long,
    val nome: String,
    val valor: Double,
    val diaVencimento: Int,
    val ativa: Boolean,
    val dataCriacao: Long,
)

@Serializable
data class GastoBackup(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val categoria: String,
    val data: Long,
    val tipo: String,
    val parcelaAtual: Int? = null,
    val totalParcelas: Int? = null,
    val grupoParcelamentoId: String? = null,
    val dataCriacao: Long,
)

@Serializable
data class SalarioExtraBackup(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long,
)

@Serializable
data class OrcamentoBackup(
    val id: Long,
    val categoria: String,
    val limiteMensal: Double,
    val ativo: Boolean,
    val dataCriacao: Long,
)

@Serializable
data class CartaoBackup(
    val id: Long,
    val limiteCentavos: Long,
    val diaFechamento: Int,
    val diaVencimento: Int,
    val dataCriacao: Long,
)

@Serializable
data class FaturaBackup(
    val id: Long,
    val cartaoId: Long,
    val mesReferencia: String,
    val dataFechamento: Long,
    val dataVencimento: Long,
    val valorPagoCentavos: Long,
    val status: String,
)

@Serializable
data class RecorrenciaCartaoBackup(
    val id: Long,
    val cartaoId: Long,
    val descricao: String,
    val valorCentavos: Long,
    val categoria: String,
    val diaCobranca: Int,
    val ativa: Boolean,
    val dataInicio: Long,
    val dataCancelamento: Long? = null,
)

@Serializable
data class CompraCartaoBackup(
    val id: Long,
    val cartaoId: Long,
    val recorrenciaId: Long? = null,
    val mesReferenciaCobranca: String? = null,
    val descricao: String,
    val valorTotalCentavos: Long,
    val categoria: String,
    val data: Long,
    val tipo: String,
    val totalParcelas: Int? = null,
    val estornada: Boolean = false,
    val dataCriacao: Long,
)

@Serializable
data class ParcelaCartaoBackup(
    val id: Long,
    val compraId: Long,
    val faturaId: Long,
    val numeroParcela: Int,
    val valorCentavos: Long,
)

@Serializable
data class PagamentoFaturaBackup(
    val id: Long,
    val faturaId: Long,
    val valorCentavos: Long,
    val data: Long,
    val estornado: Boolean = false,
    val dataEstorno: Long? = null,
    val dataCriacao: Long,
)
