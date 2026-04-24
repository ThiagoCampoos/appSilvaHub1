package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.repository.SalarioRepository
import kotlinx.coroutines.flow.Flow

class ObterSalarioDoMesUseCase(
    private val salarioRepository: SalarioRepository,
) {
    operator fun invoke(mesAno: String): Flow<Salario?> {
        return salarioRepository.getSalarioDoMes(mesAno)
    }
}

