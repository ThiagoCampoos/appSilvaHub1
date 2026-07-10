package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.repository.ContaFixaRepository
import kotlinx.coroutines.flow.Flow

class ObterContaFixaPorIdUseCase(
    private val repository: ContaFixaRepository,
) {
    operator fun invoke(id: Long): Flow<ContaFixa?> = repository.getContaPorId(id)
}
