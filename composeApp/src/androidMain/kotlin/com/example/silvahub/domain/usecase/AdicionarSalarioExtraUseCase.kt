package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.SalarioExtra
import com.example.silvahub.domain.repository.SalarioExtraRepository

class AdicionarSalarioExtraUseCase(
    private val repository: SalarioExtraRepository,
) {
    suspend operator fun invoke(extra: SalarioExtra): Long {
        require(extra.descricao.isNotBlank()) { "Descrição não pode ser vazia" }
        require(extra.valor > 0.0) { "Valor deve ser maior que zero" }
        return repository.salvar(extra)
    }
}
