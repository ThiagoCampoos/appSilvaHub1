package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.repository.ContaFixaRepository
import kotlinx.coroutines.flow.Flow

class ObterContasFixasUseCase(
    private val contaFixaRepository: ContaFixaRepository,
) {
    operator fun invoke(apenasAtivas: Boolean = true): Flow<List<ContaFixa>> {
        return if (apenasAtivas) {
            contaFixaRepository.getContasFixasAtivas()
        } else {
            contaFixaRepository.getTodasAsContas()
        }
    }
}

