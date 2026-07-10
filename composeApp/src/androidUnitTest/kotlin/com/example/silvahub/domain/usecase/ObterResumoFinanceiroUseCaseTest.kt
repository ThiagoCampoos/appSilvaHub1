package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.repository.ContaFixaRepository
import com.example.silvahub.domain.repository.SalarioExtraRepository
import com.example.silvahub.domain.repository.SalarioRepository
import com.example.silvahub.domain.model.SalarioExtra
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeSalarioRepository(private val salario: Salario?) : SalarioRepository {
    override suspend fun salvarSalario(salario: Salario) = 1L
    override suspend fun atualizarSalario(salario: Salario) = Unit
    override suspend fun deletarSalario(salario: Salario) = Unit
    override suspend fun deletarSalarioPorId(id: Long) = Unit
    override fun getSalarioDoMes(mesAno: String) = flowOf(salario)
    override fun getTodosSalarios() = flowOf(emptyList<Salario>())
    override fun getUltimoSalario() = flowOf(salario)
}

private class FakeSalarioExtraRepository(private val total: Double) : SalarioExtraRepository {
    override suspend fun salvar(extra: SalarioExtra) = 1L
    override suspend fun deletarPorId(id: Long) = Unit
    override fun getPorMes(mesAno: String) = flowOf(emptyList<SalarioExtra>())
    override fun getTotalPorMes(mesAno: String) = flowOf(total)
}

private class FakeContaFixaRepository(private val total: Double) : ContaFixaRepository {
    override suspend fun salvarConta(conta: ContaFixa) = 1L
    override suspend fun atualizarConta(conta: ContaFixa) = Unit
    override suspend fun deletarConta(conta: ContaFixa) = Unit
    override suspend fun deletarContaPorId(id: Long) = Unit
    override fun getContasFixasAtivas() = flowOf(emptyList<ContaFixa>())
    override fun getTotalContasFixasAtivas() = flowOf(total)
    override fun getTodasAsContas() = flowOf(emptyList<ContaFixa>())
    override fun getContaPorId(id: Long): Flow<ContaFixa?> = flowOf(null)
}

class ObterResumoFinanceiroUseCaseTest {
    @Test
    fun calculaSaldoCorretamente() = runTest {
        val useCase = ObterResumoFinanceiroUseCase(
            salarioRepository = FakeSalarioRepository(Salario(valor = 5000.0, mesReferencia = "2026-07")),
            salarioExtraRepository = FakeSalarioExtraRepository(500.0),
            contaFixaRepository = FakeContaFixaRepository(1500.0),
            gastoRepository = FakeGastoRepository(
                listOf(
                    com.example.silvahub.domain.model.Gasto(
                        id = 1,
                        descricao = "Mercado",
                        valor = 800.0,
                        categoria = com.example.silvahub.domain.model.ECategoriaGasto.ALIMENTACAO,
                        data = System.currentTimeMillis(),
                    ),
                ),
            ),
        )
        val resumo = useCase("2026-07").first()
        // 5000 + 500 - 1500 - 800 = 3200
        assertEquals(3200.0, resumo.saldoDisponivel, 0.01)
        assertEquals(5000.0, resumo.salario, 0.01)
        assertEquals(500.0, resumo.salariosExtras, 0.01)
    }
}
