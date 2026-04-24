package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.repository.SalarioRepository
import kotlinx.coroutines.flow.Flow

class ObterUltimoSalarioUseCase(
    private val salarioRepository: SalarioRepository,
) {
    operator fun invoke(): Flow<Salario?> {
        return salarioRepository.getUltimoSalario()
    }
}

