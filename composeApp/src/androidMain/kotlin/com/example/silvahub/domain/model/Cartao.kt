package com.example.silvahub.domain.model

data class Cartao(
    val id: Long = CARTAO_UNICO_ID,
    val limiteCentavos: Long,
    val diaFechamento: Int,
    val diaVencimento: Int,
    val dataCriacao: Long = System.currentTimeMillis(),
) {
    companion object {
        const val CARTAO_UNICO_ID = 1L
    }
}

data class ResumoLimite(
    val limiteTotalCentavos: Long,
    val limiteUtilizadoCentavos: Long,
    val limiteDisponivelCentavos: Long,
) {
    val isLimiteExcedido: Boolean get() = limiteDisponivelCentavos <= 0
}
