package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.SalarioExtra
import com.example.silvahub.domain.repository.SalarioExtraRepository
import kotlinx.coroutines.flow.Flow

class ObterSalariosExtrasUseCase(
    private val repository: SalarioExtraRepository,
) {
    operator fun invoke(mesAno: String): Flow<List<SalarioExtra>> = repository.getPorMes(mesAno)
}
