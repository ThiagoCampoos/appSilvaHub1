package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow

class ObterGastoPorIdUseCase(
    private val gastoRepository: GastoRepository,
) {
    operator fun invoke(id: Long): Flow<Gasto?> = gastoRepository.getGastoPorId(id)
}
