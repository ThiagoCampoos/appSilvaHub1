package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository

class AdicionarGastoUseCase(
    private val gastoRepository: GastoRepository,
) {
    suspend operator fun invoke(gasto: Gasto): Long {
        require(gasto.descricao.isNotBlank()) { "Descrição não pode ser vazia" }
        require(gasto.valor > 0.0) { "Valor deve ser maior que zero" }
        return gastoRepository.salvarGasto(gasto)
    }
}
