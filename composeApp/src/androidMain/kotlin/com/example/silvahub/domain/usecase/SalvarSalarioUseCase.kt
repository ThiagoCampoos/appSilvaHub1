package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.repository.SalarioRepository

class SalvarSalarioUseCase(
    private val salarioRepository: SalarioRepository,
) {
    suspend operator fun invoke(salario: Salario): Long {
        require(salario.valor > 0) { "Salario deve ser maior que zero" }
        require(salario.mesReferencia.matches(Regex("^\\d{4}-\\d{2}$"))) {
            "Mes de referencia deve estar no formato YYYY-MM"
        }
        return salarioRepository.salvarSalario(salario)
    }
}

