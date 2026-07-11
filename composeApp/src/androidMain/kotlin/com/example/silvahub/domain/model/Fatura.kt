package com.example.silvahub.domain.model

enum class EStatusFatura {
    ABERTA,
    PAGA,
}

data class Fatura(
    val id: Long = 0,
    val cartaoId: Long,
    val mesReferencia: String,
    val dataFechamento: Long,
    val dataVencimento: Long,
    val valorPagoCentavos: Long = 0,
    val status: EStatusFatura = EStatusFatura.ABERTA,
)

data class FaturaDetalhe(
    val fatura: Fatura,
    val valorTotalCentavos: Long,
    val saldoPendenteCentavos: Long,
    val parcelas: List<ParcelaCartaoComCompra> = emptyList(),
    val pagamentos: List<PagamentoFatura> = emptyList(),
)

data class ParcelaCartaoComCompra(
    val parcela: ParcelaCartao,
    val compra: CompraCartao,
)
