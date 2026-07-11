package com.example.silvahub.domain.model

enum class ETipoCompraCartao {
    CREDITO_AVISTA,
    CREDITO_PARCELADO,
    CREDITO_RECORRENTE,
    AJUSTE_ESTORNO,
}

data class CompraCartao(
    val id: Long = 0,
    val cartaoId: Long,
    val recorrenciaId: Long? = null,
    val mesReferenciaCobranca: String? = null,
    val descricao: String,
    val valorTotalCentavos: Long,
    val categoria: ECategoriaGasto,
    val data: Long,
    val tipo: ETipoCompraCartao,
    val totalParcelas: Int? = null,
    val estornada: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis(),
)

data class ParcelaCartao(
    val id: Long = 0,
    val compraId: Long,
    val faturaId: Long,
    val numeroParcela: Int,
    val valorCentavos: Long,
)
