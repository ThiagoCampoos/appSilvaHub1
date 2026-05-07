package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow

class ObterGastoDoMesUseCase(
    private val gastoRepository: GastoRepository,
) {
    operator fun invoke(mesAno: String): Flow<List<Gasto>> {
        return gastoRepository.getGastoDoMes(mesAno)
    }
}
