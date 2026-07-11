package com.example.silvahub.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val salarios: List<SalarioBackup> = emptyList(),
    val contasFixas: List<ContaFixaBackup> = emptyList(),
    val gastos: List<GastoBackup> = emptyList(),
    val salariosExtras: List<SalarioExtraBackup> = emptyList(),
    val orcamentos: List<OrcamentoBackup> = emptyList(),
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
