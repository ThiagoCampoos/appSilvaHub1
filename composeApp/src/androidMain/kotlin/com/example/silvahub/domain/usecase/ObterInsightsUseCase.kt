package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.InsightFinanceiro
import com.example.silvahub.domain.model.label
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class ObterInsightsUseCase(
    private val gastoRepository: GastoRepository,
) {
    operator fun invoke(mesAno: String = DateUtils.mesReferenciaAtual()): Flow<List<InsightFinanceiro>> {
        val mesAnterior = DateUtils.previousMesAno(mesAno)
        val totalAtual = gastoRepository.getTotalDoMes(mesAno)
        val totalAnterior = gastoRepository.getTotalDoMes(mesAnterior)

        return combine(totalAtual, totalAnterior) { atual, anterior ->
            buildList {
                if (anterior > 0.0) {
                    val variacao = ((atual - anterior) / anterior) * 100.0
                    val sinal = if (variacao >= 0) "+" else ""
                    add(
                        InsightFinanceiro(
                            titulo = "Total do mês",
                            descricao = "Gastos $sinal${"%.0f".format(variacao)}% vs mês passado",
                            percentualVariacao = variacao,
                        ),
                    )
                } else if (atual > 0.0) {
                    add(
                        InsightFinanceiro(
                            titulo = "Total do mês",
                            descricao = "Primeiro mês com gastos registrados",
                            percentualVariacao = null,
                        ),
                    )
                }

                ECategoriaGasto.entries.forEach { categoria ->
                    // Leituras pontuais via first() não cabem aqui; usamos totais já carregados
                    // Comparativo detalhado por categoria fica no fluxo abaixo via combine expandido
                }
            }
        }.combine(categoriaInsights(mesAno, mesAnterior)) { gerais, porCat ->
            gerais + porCat
        }
    }

    private fun categoriaInsights(mesAno: String, mesAnterior: String): Flow<List<InsightFinanceiro>> {
        val flows = ECategoriaGasto.entries.map { categoria ->
            combine(
                gastoRepository.getTotalPorCategoriaNoMes(categoria, mesAno),
                gastoRepository.getTotalPorCategoriaNoMes(categoria, mesAnterior),
            ) { atual, anterior -> Triple(categoria, atual, anterior) }
        }
        return combine(flows) { values ->
            values.mapNotNull { (categoria, atual, anterior) ->
                if (anterior > 0.0 && (atual > 0.0 || anterior > 0.0)) {
                    val variacao = ((atual - anterior) / anterior) * 100.0
                    val sinal = if (variacao >= 0) "+" else ""
                    InsightFinanceiro(
                        titulo = categoria.label(),
                        descricao = "$sinal${"%.0f".format(variacao)}% vs mês passado",
                        percentualVariacao = variacao,
                    )
                } else {
                    null
                }
            }
        }
    }
}
