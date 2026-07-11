package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow

class ObterUltimosGastosUseCase(
    private val gastoRepository: GastoRepository,
) {
    operator fun invoke(limit: Int = 5): Flow<List<Gasto>> {
        return gastoRepository.getUltimosGastos(limit)
    }
}
