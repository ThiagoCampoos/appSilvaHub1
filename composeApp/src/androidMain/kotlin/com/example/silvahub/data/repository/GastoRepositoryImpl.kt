package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.GastoDao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GastoRepositoryImpl(
    private val gastoDao: GastoDao,
) : GastoRepository {

    override suspend fun salvarGasto(gasto: Gasto): Long {
        return gastoDao.inserir(GastoMapper.toEntity(gasto))
    }

    override suspend fun salvarGastos(gastos: List<Gasto>): List<Long> {
        return gastoDao.inserirTodos(gastos.map(GastoMapper::toEntity))
    }

    override suspend fun atualizarGasto(gasto: Gasto) {
        gastoDao.atualizar(GastoMapper.toEntity(gasto))
    }

    override suspend fun deletarGasto(gasto: Gasto) {
        gastoDao.deletar(GastoMapper.toEntity(gasto))
    }

    override suspend fun deletarGastoPorId(id: Long) {
        gastoDao.deletarPorId(id)
    }

    override suspend fun deletarParcelasRestantes(grupoId: String, dataMinima: Long) {
        gastoDao.deletarParcelasRestantes(grupoId, dataMinima)
    }

    override suspend fun getGastoPorIdOnce(id: Long): Gasto? {
        return gastoDao.getGastoPorIdOnce(id)?.let(GastoMapper::toDomain)
    }

    override fun getGastoDoMes(mesAno: String): Flow<List<Gasto>> {
        val (inicio, fim) = DateUtils.mesAnoToRange(mesAno)
        return gastoDao.getGastosPorPeriodo(inicio, fim).map { list ->
            list.map(GastoMapper::toDomain)
        }
    }

    override fun getTodosGastos(): Flow<List<Gasto>> {
        return gastoDao.getTodosGastos().map { list ->
            list.map(GastoMapper::toDomain)
        }
    }

    override fun getUltimosGastos(limit: Int): Flow<List<Gasto>> {
        return gastoDao.getUltimosGastos(limit).map { list ->
            list.map(GastoMapper::toDomain)
        }
    }

    override fun getGastoPorId(id: Long): Flow<Gasto?> {
        return gastoDao.getGastoPorId(id).map { entity ->
            entity?.let(GastoMapper::toDomain)
        }
    }

    override fun getTotalDoMes(mesAno: String): Flow<Double> {
        val (inicio, fim) = DateUtils.mesAnoToRange(mesAno)
        return gastoDao.gastoTotalPorPeriodo(inicio, fim)
    }

    override fun getTotalPorCategoriaNoMes(categoria: ECategoriaGasto, mesAno: String): Flow<Double> {
        val (inicio, fim) = DateUtils.mesAnoToRange(mesAno)
        return gastoDao.gastoTotalPorCategoriaNoPeriodo(categoria, inicio, fim)
    }
}
