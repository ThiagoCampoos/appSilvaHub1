package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import java.util.UUID

class AdicionarGastoRecorrenteUseCase(
    private val gastoRepository: GastoRepository,
) {
    /**
     * Cria o gasto do mês atual e pré-lança os próximos 11 meses (12 no total).
     */
    suspend operator fun invoke(
        descricao: String,
        valor: Double,
        categoria: com.example.silvahub.domain.model.ECategoriaGasto,
        dataInicio: Long,
        mesesAdiante: Int = 11,
    ): List<Long> {
        require(descricao.isNotBlank()) { "Descrição não pode ser vazia" }
        require(valor > 0.0) { "Valor deve ser maior que zero" }

        val grupoId = UUID.randomUUID().toString()
        val total = mesesAdiante + 1
        val gastos = (0 until total).map { offset ->
            Gasto(
                descricao = descricao,
                valor = valor,
                categoria = categoria,
                data = DateUtils.addMonths(dataInicio, offset),
                tipo = ETipoGasto.RECORRENTE,
                grupoParcelamentoId = grupoId,
            )
        }
        return gastoRepository.salvarGastos(gastos)
    }
}
