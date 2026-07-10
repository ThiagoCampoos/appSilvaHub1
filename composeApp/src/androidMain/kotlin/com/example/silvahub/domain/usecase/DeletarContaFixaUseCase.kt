package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.ContaFixaRepository
import kotlin.require

class DeletarContaFixaUseCase(
    private val contaFixaRepository: ContaFixaRepository,
) {
    suspend operator fun invoke(
        id: String
    ) {
        require(id.isNotBlank()) { "Id da conta fixa invalido" }
        contaFixaRepository.deletarContaPorId(id)
    }
}
