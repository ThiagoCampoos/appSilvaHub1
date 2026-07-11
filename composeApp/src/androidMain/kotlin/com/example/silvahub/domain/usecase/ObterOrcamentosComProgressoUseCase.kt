package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.OrcamentoComProgresso
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.domain.repository.OrcamentoRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObterOrcamentosComProgressoUseCase(
    private val orcamentoRepository: OrcamentoRepository,
    private val gastoRepository: GastoRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(mesAno: String = DateUtils.mesReferenciaAtual()): Flow<List<OrcamentoComProgresso>> {
        return orcamentoRepository.getAtivos().flatMapLatest { orcamentos ->
            if (orcamentos.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = orcamentos.map { orcamento ->
                    gastoRepository.getTotalPorCategoriaNoMes(orcamento.categoria, mesAno)
                        .map { gasto -> OrcamentoComProgresso(orcamento, gasto) }
                }
                combine(flows) { it.toList() }
            }
        }
    }
}
