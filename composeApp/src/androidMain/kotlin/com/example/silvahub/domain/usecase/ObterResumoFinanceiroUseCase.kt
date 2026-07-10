package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ResumoFinanceiro
import com.example.silvahub.domain.repository.ContaFixaRepository
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.domain.repository.SalarioExtraRepository
import com.example.silvahub.domain.repository.SalarioRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObterResumoFinanceiroUseCase(
    private val salarioRepository: SalarioRepository,
    private val salarioExtraRepository: SalarioExtraRepository,
    private val contaFixaRepository: ContaFixaRepository,
    private val gastoRepository: GastoRepository,
) {
    operator fun invoke(mesAno: String = DateUtils.mesReferenciaAtual()): Flow<ResumoFinanceiro> {
        return combine(
            salarioRepository.getSalarioDoMes(mesAno),
            salarioExtraRepository.getTotalPorMes(mesAno),
            contaFixaRepository.getTotalContasFixasAtivas(),
            gastoRepository.getTotalDoMes(mesAno),
        ) { salario, extras, contas, gastos ->
            val salarioValor = salario?.valor ?: 0.0
            val saldo = salarioValor + extras - contas - gastos
            val dias = DateUtils.daysRemainingInMonth()
            ResumoFinanceiro(
                mesReferencia = mesAno,
                salario = salarioValor,
                salariosExtras = extras,
                contasFixas = contas,
                gastos = gastos,
                saldoDisponivel = saldo,
                gastoDiarioSugerido = if (saldo > 0) saldo / dias else 0.0,
            )
        }
    }
}
