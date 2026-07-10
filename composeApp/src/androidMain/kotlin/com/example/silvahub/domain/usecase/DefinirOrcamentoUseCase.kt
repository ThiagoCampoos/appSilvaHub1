package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Orcamento
import com.example.silvahub.domain.repository.OrcamentoRepository

class DefinirOrcamentoUseCase(
    private val repository: OrcamentoRepository,
) {
    suspend operator fun invoke(orcamento: Orcamento): Long {
        require(orcamento.limiteMensal > 0.0) { "Limite deve ser maior que zero" }
        return repository.salvar(orcamento)
    }
}
