package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.OrcamentoRepository

class DeletarOrcamentoUseCase(
    private val repository: OrcamentoRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deletarPorId(id)
}
