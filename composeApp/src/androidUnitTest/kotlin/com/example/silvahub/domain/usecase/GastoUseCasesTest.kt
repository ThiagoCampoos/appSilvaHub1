package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AdicionarGastoUseCaseTest {
    @Test
    fun rejeitaValorInvalido() = runTest {
        val useCase = AdicionarGastoUseCase(FakeGastoRepository())
        assertFailsWith<IllegalArgumentException> {
            useCase(
                com.example.silvahub.domain.model.Gasto(
                    descricao = "Teste",
                    valor = 0.0,
                    categoria = ECategoriaGasto.OUTROS,
                    data = System.currentTimeMillis(),
                ),
            )
        }
    }

    @Test
    fun salvaGastoValido() = runTest {
        val repo = FakeGastoRepository()
        val useCase = AdicionarGastoUseCase(repo)
        val id = useCase(
            com.example.silvahub.domain.model.Gasto(
                descricao = "Mercado",
                valor = 50.0,
                categoria = ECategoriaGasto.ALIMENTACAO,
                data = System.currentTimeMillis(),
            ),
        )
        assertTrue(id > 0)
        assertEquals(1, repo.getTodosGastos().first().size)
    }
}

class AdicionarGastoParceladoUseCaseTest {
    @Test
    fun criaTodasAsParcelas() = runTest {
        val repo = FakeGastoRepository()
        val useCase = AdicionarGastoParceladoUseCase(repo)
        val ids = useCase(
            descricao = "Notebook",
            valorParcela = 200.0,
            categoria = ECategoriaGasto.OUTROS,
            dataPrimeiraParcela = System.currentTimeMillis(),
            totalParcelas = 5,
        )
        assertEquals(5, ids.size)
        val todos = repo.getTodosGastos().first()
        assertEquals(5, todos.size)
        assertTrue(todos.all { it.grupoParcelamentoId != null })
        assertEquals(ETipoGasto.FIXO, todos.first().tipo)
        assertEquals("Notebook (1/5)", todos.minBy { it.parcelaAtual ?: 0 }.descricao)
    }
}

class ObterInsightsUseCaseTest {
    @Test
    fun retornaVazioSemDados() = runTest {
        val useCase = ObterInsightsUseCase(FakeGastoRepository())
        val insights = useCase("2026-07").first()
        assertTrue(insights.isEmpty())
    }
}
