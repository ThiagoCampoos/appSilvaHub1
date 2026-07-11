package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Fake em memória para testes unitários.
 */
class FakeGastoRepository(
    initial: List<Gasto> = emptyList(),
) : GastoRepository {
    private val gastos = MutableStateFlow(initial)

    override suspend fun salvarGasto(gasto: Gasto): Long {
        val id = (gastos.value.maxOfOrNull { it.id } ?: 0L) + 1
        gastos.value = gastos.value + gasto.copy(id = id)
        return id
    }

    override suspend fun salvarGastos(gastosNovos: List<Gasto>): List<Long> {
        return gastosNovos.map { salvarGasto(it) }
    }

    override suspend fun atualizarGasto(gasto: Gasto) {
        gastos.value = gastos.value.map { if (it.id == gasto.id) gasto else it }
    }

    override suspend fun deletarGasto(gasto: Gasto) {
        gastos.value = gastos.value.filterNot { it.id == gasto.id }
    }

    override suspend fun deletarGastoPorId(id: Long) {
        gastos.value = gastos.value.filterNot { it.id == id }
    }

    override suspend fun deletarParcelasRestantes(grupoId: String, dataMinima: Long) {
        gastos.value = gastos.value.filterNot {
            it.grupoParcelamentoId == grupoId && it.data >= dataMinima
        }
    }

    override suspend fun getGastoPorIdOnce(id: Long): Gasto? = gastos.value.find { it.id == id }

    override fun getGastoDoMes(mesAno: String): Flow<List<Gasto>> = gastos.map { list ->
        list.filter {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.data }
            val key = "%04d-%02d".format(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1)
            key == mesAno
        }
    }

    override fun getTodosGastos(): Flow<List<Gasto>> = gastos

    override fun getUltimosGastos(limit: Int): Flow<List<Gasto>> = gastos.map { it.take(limit) }

    override fun getGastoPorId(id: Long): Flow<Gasto?> = gastos.map { list -> list.find { it.id == id } }

    override fun getTotalDoMes(mesAno: String): Flow<Double> = getGastoDoMes(mesAno).map { list -> list.sumOf { it.valor } }

    override fun getTotalPorCategoriaNoMes(categoria: ECategoriaGasto, mesAno: String): Flow<Double> {
        return getGastoDoMes(mesAno).map { list -> list.filter { it.categoria == categoria }.sumOf { it.valor } }
    }
}
