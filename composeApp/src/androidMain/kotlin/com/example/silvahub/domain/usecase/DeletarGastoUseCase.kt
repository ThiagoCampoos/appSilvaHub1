package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.GastoRepository

class DeletarGastoUseCase(
    private val gastoRepository: GastoRepository,
) {
    suspend operator fun invoke(id: Long, excluirParcelasRestantes: Boolean = false) {
        if (excluirParcelasRestantes) {
            val gasto = gastoRepository.getGastoPorIdOnce(id)
            val grupoId = gasto?.grupoParcelamentoId
            if (grupoId != null && gasto != null) {
                gastoRepository.deletarParcelasRestantes(grupoId, gasto.data)
                return
            }
        }
        gastoRepository.deletarGastoPorId(id)
    }
}
