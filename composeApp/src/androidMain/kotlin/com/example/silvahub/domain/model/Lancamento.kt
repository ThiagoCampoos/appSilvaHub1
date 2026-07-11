package com.example.silvahub.domain.model

enum class ETipoLancamento {
    DEBITO_AVISTA,
    CREDITO_AVISTA,
    CREDITO_PARCELADO,
    CREDITO_RECORRENTE,
}

fun ETipoLancamento.label(): String = when (this) {
    ETipoLancamento.DEBITO_AVISTA -> "Débito à vista"
    ETipoLancamento.CREDITO_AVISTA -> "Crédito à vista"
    ETipoLancamento.CREDITO_PARCELADO -> "Crédito parcelado"
    ETipoLancamento.CREDITO_RECORRENTE -> "Crédito recorrente"
}


data class Lancamento(
    val id: String,
    val descricao: String,
    val valor: Double,
    val valorCentavos: Long? = null,
    val categoria: ECategoriaGasto,
    val data: Long,
    val tipoLancamento: ETipoLancamento,
    val parcelaAtual: Int? = null,
    val totalParcelas: Int? = null,
    val gastoId: Long? = null,
    val compraCartaoId: Long? = null,
    val parcelaCartaoId: Long? = null,
    val faturaId: Long? = null,
    val grupoParcelamentoId: String? = null,
    val tipoGastoLegado: ETipoGasto? = null,
)
