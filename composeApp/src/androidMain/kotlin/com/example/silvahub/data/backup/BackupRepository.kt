package com.example.silvahub.data.backup

import androidx.room.withTransaction
import com.example.silvahub.data.local.dao.CartaoDao
import com.example.silvahub.data.local.dao.ContaFixaDao
import com.example.silvahub.data.local.dao.FaturaDao
import com.example.silvahub.data.local.dao.GastoDao
import com.example.silvahub.data.local.dao.OrcamentoDao
import com.example.silvahub.data.local.dao.PagamentoFaturaDao
import com.example.silvahub.data.local.dao.SalarioDao
import com.example.silvahub.data.local.dao.SalarioExtraDao
import com.example.silvahub.data.local.database.AppDatabase
import com.example.silvahub.data.local.entity.CartaoEntity
import com.example.silvahub.data.local.entity.CompraCartaoEntity
import com.example.silvahub.data.local.entity.ContaFixaEntity
import com.example.silvahub.data.local.entity.FaturaEntity
import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.data.local.entity.OrcamentoEntity
import com.example.silvahub.data.local.entity.PagamentoFaturaEntity
import com.example.silvahub.data.local.entity.ParcelaCartaoEntity
import com.example.silvahub.data.local.entity.RecorrenciaCartaoEntity
import com.example.silvahub.data.local.entity.SalarioEntity
import com.example.silvahub.data.local.entity.SalarioExtraEntity
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class BackupRepository(
    private val database: AppDatabase,
    private val salarioDao: SalarioDao,
    private val contaFixaDao: ContaFixaDao,
    private val gastoDao: GastoDao,
    private val salarioExtraDao: SalarioExtraDao,
    private val orcamentoDao: OrcamentoDao,
    private val cartaoDao: CartaoDao,
    private val faturaDao: FaturaDao,
    private val pagamentoFaturaDao: PagamentoFaturaDao,
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): String {
        val payload = BackupPayload(
            version = 2,
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
            cartoes = listOfNotNull(cartaoDao.getUnicoOnce()).map {
                CartaoBackup(it.id, it.limiteCentavos, it.diaFechamento, it.diaVencimento, it.dataCriacao)
            },
            faturas = faturaDao.getTodasFaturas().first().map {
                FaturaBackup(
                    it.id, it.cartaoId, it.mesReferencia, it.dataFechamento,
                    it.dataVencimento, it.valorPagoCentavos, it.status,
                )
            },
            recorrenciasCartao = faturaDao.getTodasRecorrencias().first().map {
                RecorrenciaCartaoBackup(
                    it.id, it.cartaoId, it.descricao, it.valorCentavos, it.categoria,
                    it.diaCobranca, it.ativa, it.dataInicio, it.dataCancelamento,
                )
            },
            comprasCartao = faturaDao.getTodasCompras().first().map {
                CompraCartaoBackup(
                    it.id, it.cartaoId, it.recorrenciaId, it.mesReferenciaCobranca,
                    it.descricao, it.valorTotalCentavos, it.categoria, it.data, it.tipo,
                    it.totalParcelas, it.estornada, it.dataCriacao,
                )
            },
            parcelasCartao = faturaDao.getTodasParcelasOnce().map {
                ParcelaCartaoBackup(it.id, it.compraId, it.faturaId, it.numeroParcela, it.valorCentavos)
            },
            pagamentosFatura = pagamentoFaturaDao.getTodos().first().map {
                PagamentoFaturaBackup(
                    it.id, it.faturaId, it.valorCentavos, it.data,
                    it.estornado, it.dataEstorno, it.dataCriacao,
                )
            },
        )
        return json.encodeToString(BackupPayload.serializer(), payload)
    }

    suspend fun importFromJson(raw: String) {
        val payload = json.decodeFromString(BackupPayload.serializer(), raw)

        database.withTransaction {
            // Ordem: filhos antes dos pais na limpeza
            pagamentoFaturaDao.deletarTodos()
            faturaDao.deletarTodasParcelas()
            faturaDao.deletarTodasCompras()
            faturaDao.deletarTodasRecorrencias()
            faturaDao.deletarTodasFaturas()
            cartaoDao.deletarTodos()

            gastoDao.getTodosGastos().first().forEach { gastoDao.deletar(it) }
            contaFixaDao.getTodasAsContas().first().forEach { contaFixaDao.deletar(it) }
            salarioDao.getTodosSalarios().first().forEach { salarioDao.deletar(it) }
            salarioExtraDao.getTodos().first().forEach { salarioExtraDao.deletar(it) }
            orcamentoDao.getTodos().first().forEach { orcamentoDao.deletar(it) }

            payload.salarios.forEach {
                salarioDao.inserir(SalarioEntity(it.id, it.valor, it.mesReferencia, it.dataCriacao))
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

            // Cartão: pais antes dos filhos
            payload.cartoes.forEach {
                cartaoDao.inserir(
                    CartaoEntity(it.id, it.limiteCentavos, it.diaFechamento, it.diaVencimento, it.dataCriacao),
                )
            }
            payload.recorrenciasCartao.forEach {
                faturaDao.inserirRecorrencia(
                    RecorrenciaCartaoEntity(
                        it.id, it.cartaoId, it.descricao, it.valorCentavos, it.categoria,
                        it.diaCobranca, it.ativa, it.dataInicio, it.dataCancelamento,
                    ),
                )
            }
            payload.faturas.forEach {
                faturaDao.inserirFatura(
                    FaturaEntity(
                        it.id, it.cartaoId, it.mesReferencia, it.dataFechamento,
                        it.dataVencimento, it.valorPagoCentavos, it.status,
                    ),
                )
            }
            payload.comprasCartao.forEach {
                faturaDao.inserirCompra(
                    CompraCartaoEntity(
                        it.id, it.cartaoId, it.recorrenciaId, it.mesReferenciaCobranca,
                        it.descricao, it.valorTotalCentavos, it.categoria, it.data, it.tipo,
                        it.totalParcelas, it.estornada, it.dataCriacao,
                    ),
                )
            }
            if (payload.parcelasCartao.isNotEmpty()) {
                faturaDao.inserirParcelas(
                    payload.parcelasCartao.map {
                        ParcelaCartaoEntity(
                            it.id, it.compraId, it.faturaId, it.numeroParcela, it.valorCentavos,
                        )
                    },
                )
            }
            payload.pagamentosFatura.forEach {
                pagamentoFaturaDao.inserir(
                    PagamentoFaturaEntity(
                        it.id, it.faturaId, it.valorCentavos, it.data,
                        it.estornado, it.dataEstorno, it.dataCriacao,
                    ),
                )
            }
        }
    }
}
