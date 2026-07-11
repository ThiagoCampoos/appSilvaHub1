package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import java.util.UUID

class AdicionarGastoParceladoUseCase(
    private val gastoRepository: GastoRepository,
) {
    suspend operator fun invoke(
        descricao: String,
        valorParcela: Double,
        categoria: com.example.silvahub.domain.model.ECategoriaGasto,
        dataPrimeiraParcela: Long,
        totalParcelas: Int,
    ): List<Long> {
        require(descricao.isNotBlank()) { "Descrição não pode ser vazia" }
        require(valorParcela > 0.0) { "Valor deve ser maior que zero" }
        require(totalParcelas in 2..48) { "Parcelas devem estar entre 2 e 48" }

        val grupoId = UUID.randomUUID().toString()
        val gastos = (1..totalParcelas).map { parcela ->
            Gasto(
                descricao = "$descricao ($parcela/$totalParcelas)",
                valor = valorParcela,
                categoria = categoria,
                data = DateUtils.addMonths(dataPrimeiraParcela, parcela - 1),
                tipo = ETipoGasto.FIXO,
                parcelaAtual = parcela,
                totalParcelas = totalParcelas,
                grupoParcelamentoId = grupoId,
            )
        }
        return gastoRepository.salvarGastos(gastos)
    }
}
