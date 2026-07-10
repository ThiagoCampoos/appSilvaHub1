package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.SalarioExtraRepository

class DeletarSalarioExtraUseCase(
    private val repository: SalarioExtraRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deletarPorId(id)
}
