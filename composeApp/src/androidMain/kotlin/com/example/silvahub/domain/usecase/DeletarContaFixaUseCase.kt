package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.ContaFixaRepository

class DeletarContaFixaUseCase(
    private val contaFixaRepository: ContaFixaRepository,
) {
    suspend operator fun invoke(id: Long) {
        require(id > 0) { "Id da conta fixa invalido" }
        contaFixaRepository.deletarContaPorId(id)
    }
}

