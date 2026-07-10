package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.repository.ContaFixaRepository

class AtualizarContaFixaUseCase(
    private val repository: ContaFixaRepository,
) {
    suspend operator fun invoke(conta: ContaFixa) {
        require(conta.nome.isNotBlank()) { "Nome não pode ser vazio" }
        require(conta.valor > 0.0) { "Valor deve ser maior que zero" }
        require(conta.diaVencimento in 1..31) { "Dia de vencimento deve estar entre 1 e 31" }
        repository.atualizarConta(conta)
    }
}
