package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.repository.ContaFixaRepository

class AdicionarContaFixaUseCase(
    private val contaFixaRepository: ContaFixaRepository,
) {
    suspend operator fun invoke(conta: ContaFixa): Long {
        require(conta.nome.isNotBlank()) { "Nome da conta fixa e obrigatorio" }
        require(conta.valor > 0) { "Valor da conta fixa deve ser maior que zero" }
        require(conta.diaVencimento in 1..31) { "Dia de vencimento deve estar entre 1 e 31" }
        return contaFixaRepository.salvarConta(conta)
    }
}

