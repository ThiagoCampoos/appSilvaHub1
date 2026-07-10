package com.example.silvahub.domain.model

data class Orcamento(
    val id: Long = 0,
    val categoria: ECategoriaGasto,
    val limiteMensal: Double,
    val ativo: Boolean = true,
    val dataCriacao: Long = System.currentTimeMillis(),
)

data class OrcamentoComProgresso(
    val orcamento: Orcamento,
    val gastoAtual: Double,
) {
    val percentual: Float
        get() = if (orcamento.limiteMensal <= 0.0) {
            0f
        } else {
            (gastoAtual / orcamento.limiteMensal).toFloat().coerceAtLeast(0f)
        }

    val estourado: Boolean get() = gastoAtual > orcamento.limiteMensal
}

data class ResumoFinanceiro(
    val mesReferencia: String,
    val salario: Double,
    val salariosExtras: Double,
    val contasFixas: Double,
    val gastos: Double,
    val saldoDisponivel: Double,
    val gastoDiarioSugerido: Double,
)

data class InsightFinanceiro(
    val titulo: String,
    val descricao: String,
    val percentualVariacao: Double?,
)
