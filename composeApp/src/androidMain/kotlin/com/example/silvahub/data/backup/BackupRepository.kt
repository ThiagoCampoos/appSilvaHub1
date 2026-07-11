package com.example.silvahub.data.backup

import com.example.silvahub.data.local.dao.ContaFixaDao
import com.example.silvahub.data.local.dao.GastoDao
import com.example.silvahub.data.local.dao.OrcamentoDao
import com.example.silvahub.data.local.dao.SalarioDao
import com.example.silvahub.data.local.dao.SalarioExtraDao
import com.example.silvahub.data.local.entity.ContaFixaEntity
import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.data.local.entity.OrcamentoEntity
import com.example.silvahub.data.local.entity.SalarioEntity
import com.example.silvahub.data.local.entity.SalarioExtraEntity
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class BackupRepository(
    private val salarioDao: SalarioDao,
    private val contaFixaDao: ContaFixaDao,
    private val gastoDao: GastoDao,
    private val salarioExtraDao: SalarioExtraDao,
    private val orcamentoDao: OrcamentoDao,
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): String {
        val payload = BackupPayload(
            salarios = salarioDao.getTodosSalarios().first().map {
                SalarioBackup(it.id, it.valor, it.mesReferencia, it.dataCriacao)
            },
            contasFixas = contaFixaDao.getTodasAsContas().first().map {
                ContaFixaBackup(it.id, it.nome, it.valor, it.diaVencimento, it.ativa, it.dataCriacao)
            },
            gastos = gastoDao.getTodosGastos().first().map {
                GastoBackup(
                    id = it.id,
                    descricao = it.descricao,
                    valor = it.valor,
                    categoria = it.categoria.name,
                    data = it.data,
                    tipo = it.tipo.name,
                    parcelaAtual = it.parcelaAtual,
                    totalParcelas = it.totalParcelas,
                    grupoParcelamentoId = it.grupoParcelamentoId,
                    dataCriacao = it.dataCriacao,
                )
            },
            salariosExtras = salarioExtraDao.getTodos().first().map {
                SalarioExtraBackup(it.id, it.descricao, it.valor, it.mesReferencia, it.dataCriacao)
            },
            orcamentos = orcamentoDao.getTodos().first().map {
                OrcamentoBackup(it.id, it.categoria.name, it.limiteMensal, it.ativo, it.dataCriacao)
            },
        )
        return json.encodeToString(BackupPayload.serializer(), payload)
    }

    suspend fun importFromJson(raw: String) {
        val payload = json.decodeFromString(BackupPayload.serializer(), raw)

        // Limpa e reimporta (IDs preservados via REPLACE)
        gastoDao.getTodosGastos().first().forEach { gastoDao.deletar(it) }
        contaFixaDao.getTodasAsContas().first().forEach { contaFixaDao.deletar(it) }
        salarioDao.getTodosSalarios().first().forEach { salarioDao.deletar(it) }
        salarioExtraDao.getTodos().first().forEach { salarioExtraDao.deletar(it) }
        orcamentoDao.getTodos().first().forEach { orcamentoDao.deletar(it) }

        payload.salarios.forEach {
            salarioDao.inserir(
                SalarioEntity(it.id, it.valor, it.mesReferencia, it.dataCriacao),
            )
        }
        payload.contasFixas.forEach {
            contaFixaDao.inserir(
                ContaFixaEntity(it.id, it.nome, it.valor, it.diaVencimento, it.ativa, it.dataCriacao),
            )
        }
        payload.gastos.forEach {
            gastoDao.inserir(
                GastoEntity(
                    id = it.id,
                    descricao = it.descricao,
                    valor = it.valor,
                    categoria = ECategoriaGasto.valueOf(it.categoria),
                    data = it.data,
                    tipo = ETipoGasto.valueOf(it.tipo),
                    parcelaAtual = it.parcelaAtual,
                    totalParcelas = it.totalParcelas,
                    grupoParcelamentoId = it.grupoParcelamentoId,
                    dataCriacao = it.dataCriacao,
                ),
            )
        }
        payload.salariosExtras.forEach {
            salarioExtraDao.inserir(
                SalarioExtraEntity(it.id, it.descricao, it.valor, it.mesReferencia, it.dataCriacao),
            )
        }
        payload.orcamentos.forEach {
            orcamentoDao.inserir(
                OrcamentoEntity(
                    it.id,
                    ECategoriaGasto.valueOf(it.categoria),
                    it.limiteMensal,
                    it.ativo,
                    it.dataCriacao,
                ),
            )
        }
    }
}
