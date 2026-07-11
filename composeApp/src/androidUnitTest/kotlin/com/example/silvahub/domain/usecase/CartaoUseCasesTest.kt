package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.Fatura
import com.example.silvahub.domain.model.PagamentoFatura
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.model.RecorrenciaCartao
import com.example.silvahub.domain.model.ResumoLimite
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.repository.PagamentoFaturaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeCartaoRepository(
    initial: Cartao? = Cartao(
        limiteCentavos = 500_000,
        diaFechamento = 10,
        diaVencimento = 17,
    ),
) : CartaoRepository {
    private val cartaoFlow = MutableStateFlow(initial)
    var parcelasSoma = 0L
    var pagamentosSoma = 0L

    override suspend fun salvar(cartao: Cartao): Long {
        cartaoFlow.value = cartao
        return cartao.id
    }

    override suspend fun atualizar(cartao: Cartao) {
        cartaoFlow.value = cartao
    }

    override fun getUnico() = cartaoFlow
    override suspend fun getUnicoOnce() = cartaoFlow.value

    override fun getResumoLimite(cartaoId: Long): Flow<ResumoLimite> = cartaoFlow.map { c ->
        val limite = c?.limiteCentavos ?: 0L
        val utilizado = parcelasSoma - pagamentosSoma
        ResumoLimite(limite, utilizado, limite - utilizado)
    }

    override suspend fun getResumoLimiteOnce(cartaoId: Long): ResumoLimite {
        val limite = cartaoFlow.value?.limiteCentavos ?: 0L
        val utilizado = parcelasSoma - pagamentosSoma
        return ResumoLimite(limite, utilizado, limite - utilizado)
    }
}

private class FakeFaturaRepository : FaturaRepository {
    private var nextFaturaId = 1L
    private var nextCompraId = 1L
    private var nextParcelaId = 1L
    private var nextRecId = 1L

    val faturas = mutableListOf<Fatura>()
    val compras = mutableListOf<CompraCartao>()
    val parcelas = mutableListOf<ParcelaCartao>()
    val recorrencias = mutableListOf<RecorrenciaCartao>()

    override fun getFaturas(cartaoId: Long) = flowOf(faturas.filter { it.cartaoId == cartaoId })
    override fun getFaturaPorId(id: Long) = flowOf(faturas.find { it.id == id })
    override suspend fun getFaturaPorIdOnce(id: Long) = faturas.find { it.id == id }
    override suspend fun getFaturaPorMes(cartaoId: Long, mesReferencia: String) =
        faturas.find { it.cartaoId == cartaoId && it.mesReferencia == mesReferencia }

    override suspend fun obterOuCriarFatura(
        cartaoId: Long,
        mesReferencia: String,
        dataFechamento: Long,
        dataVencimento: Long,
    ): Fatura {
        getFaturaPorMes(cartaoId, mesReferencia)?.let { return it }
        val f = Fatura(
            id = nextFaturaId++,
            cartaoId = cartaoId,
            mesReferencia = mesReferencia,
            dataFechamento = dataFechamento,
            dataVencimento = dataVencimento,
        )
        faturas += f
        return f
    }

    override suspend fun atualizarFatura(fatura: Fatura) {
        val idx = faturas.indexOfFirst { it.id == fatura.id }
        if (idx >= 0) faturas[idx] = fatura
    }

    override fun somaParcelasDaFatura(faturaId: Long) =
        flowOf(parcelas.filter { it.faturaId == faturaId }.sumOf { it.valorCentavos })

    override suspend fun somaParcelasDaFaturaOnce(faturaId: Long) =
        parcelas.filter { it.faturaId == faturaId }.sumOf { it.valorCentavos }

    override suspend fun registrarCompraComParcelas(
        compra: CompraCartao,
        parcelasNovas: List<ParcelaCartao>,
    ): Long {
        val id = nextCompraId++
        compras += compra.copy(id = id)
        parcelasNovas.forEach { p ->
            parcelas += p.copy(id = nextParcelaId++, compraId = id)
        }
        return id
    }

    override fun getCompraPorId(id: Long) = flowOf(compras.find { it.id == id })
    override suspend fun getCompraPorIdOnce(id: Long) = compras.find { it.id == id }
    override fun getComprasDoCartao(cartaoId: Long) =
        flowOf(compras.filter { it.cartaoId == cartaoId })

    override suspend fun atualizarCompra(compra: CompraCartao) {
        val idx = compras.indexOfFirst { it.id == compra.id }
        if (idx >= 0) compras[idx] = compra
    }

    override suspend fun deletarCompraComParcelas(compraId: Long) {
        parcelas.removeAll { it.compraId == compraId }
        compras.removeAll { it.id == compraId }
    }

    override suspend fun getCompraRecorrenteDoMes(recorrenciaId: Long, mesReferencia: String) =
        compras.find {
            it.recorrenciaId == recorrenciaId && it.mesReferenciaCobranca == mesReferencia
        }

    override suspend fun getParcelasDaCompra(compraId: Long) =
        parcelas.filter { it.compraId == compraId }

    override fun getParcelasDaCompraFlow(compraId: Long) =
        flowOf(parcelas.filter { it.compraId == compraId })

    override fun getParcelasDaFatura(faturaId: Long) =
        flowOf(parcelas.filter { it.faturaId == faturaId })

    override suspend fun getParcelasDaFaturaOnce(faturaId: Long) =
        parcelas.filter { it.faturaId == faturaId }

    override suspend fun atualizarParcela(parcela: ParcelaCartao) {
        val idx = parcelas.indexOfFirst { it.id == parcela.id }
        if (idx >= 0) parcelas[idx] = parcela
    }

    override suspend fun deletarParcelasDaCompra(compraId: Long) {
        parcelas.removeAll { it.compraId == compraId }
    }

    override suspend fun inserirParcelas(parcelasNovas: List<ParcelaCartao>): List<Long> {
        return parcelasNovas.map { p ->
            val id = nextParcelaId++
            parcelas += p.copy(id = id)
            id
        }
    }

    override suspend fun salvarRecorrencia(recorrencia: RecorrenciaCartao): Long {
        val id = nextRecId++
        recorrencias += recorrencia.copy(id = id)
        return id
    }

    override suspend fun atualizarRecorrencia(recorrencia: RecorrenciaCartao) {
        val idx = recorrencias.indexOfFirst { it.id == recorrencia.id }
        if (idx >= 0) recorrencias[idx] = recorrencia
    }

    override suspend fun getRecorrenciaPorIdOnce(id: Long) = recorrencias.find { it.id == id }
    override fun getRecorrenciasAtivas(cartaoId: Long) =
        flowOf(recorrencias.filter { it.cartaoId == cartaoId && it.ativa })

    override suspend fun getRecorrenciasAtivasOnce(cartaoId: Long) =
        recorrencias.filter { it.cartaoId == cartaoId && it.ativa }

    override fun getTodasRecorrencias() = flowOf(recorrencias.toList())

    override suspend fun <T> withTransaction(block: suspend () -> T): T = block()
}

private class FakePagamentoRepo : PagamentoFaturaRepository {
    private var nextId = 1L
    val pagamentos = mutableListOf<PagamentoFatura>()

    override suspend fun salvar(pagamento: PagamentoFatura): Long {
        val id = nextId++
        pagamentos += pagamento.copy(id = id)
        return id
    }

    override suspend fun atualizar(pagamento: PagamentoFatura) {
        val idx = pagamentos.indexOfFirst { it.id == pagamento.id }
        if (idx >= 0) pagamentos[idx] = pagamento
    }

    override suspend fun getPorIdOnce(id: Long) = pagamentos.find { it.id == id }
    override fun getPorFatura(faturaId: Long) =
        flowOf(pagamentos.filter { it.faturaId == faturaId })

    override suspend fun getPorFaturaOnce(faturaId: Long) =
        pagamentos.filter { it.faturaId == faturaId }

    override fun somaPagamentosNoPeriodo(dataInicial: Long, dataFinal: Long) =
        flowOf(
            pagamentos.filter {
                !it.estornado && it.data in dataInicial..dataFinal
            }.sumOf { it.valorCentavos },
        )

    override fun somaPagamentosAtivosDoCartao(cartaoId: Long) =
        flowOf(pagamentos.filter { !it.estornado }.sumOf { it.valorCentavos })

    override suspend fun somaPagamentosAtivosDoCartaoOnce(cartaoId: Long) =
        pagamentos.filter { !it.estornado }.sumOf { it.valorCentavos }
}

class CartaoUseCasesTest {
    @Test
    fun salvarCartaoComAvisoQuandoLimiteAbaixoUtilizado() = runTest {
        val cartaoRepo = FakeCartaoRepository()
        cartaoRepo.parcelasSoma = 300_000
        val useCase = SalvarCartaoUseCase(cartaoRepo)
        val result = useCase(200_000, 10, 17)
        assertTrue(result.avisoLimiteAbaixoUtilizado)
        assertEquals(200_000, cartaoRepo.getUnicoOnce()?.limiteCentavos)
    }

    @Test
    fun compraCreditoAvistaComprometeLimiteIntegral() = runTest {
        val cartaoRepo = FakeCartaoRepository()
        val faturaRepo = FakeFaturaRepository()
        val useCase = RegistrarCompraCartaoUseCase(cartaoRepo, faturaRepo)

        val id = useCase(
            descricao = "Mercado",
            valorCentavos = 15_000,
            categoria = ECategoriaGasto.ALIMENTACAO,
            data = System.currentTimeMillis(),
            tipo = ETipoCompraCartao.CREDITO_AVISTA,
        )
        assertTrue(id > 0)
        assertEquals(1, faturaRepo.compras.size)
        assertEquals(1, faturaRepo.parcelas.size)
        assertEquals(15_000, faturaRepo.parcelas.first().valorCentavos)
    }

    @Test
    fun compraParceladaDistribuiParcelas() = runTest {
        val cartaoRepo = FakeCartaoRepository()
        val faturaRepo = FakeFaturaRepository()
        val useCase = RegistrarCompraCartaoUseCase(cartaoRepo, faturaRepo)

        useCase(
            descricao = "Notebook",
            valorCentavos = 10_000,
            categoria = ECategoriaGasto.OUTROS,
            data = System.currentTimeMillis(),
            tipo = ETipoCompraCartao.CREDITO_PARCELADO,
            totalParcelas = 3,
        )
        assertEquals(3, faturaRepo.parcelas.size)
        assertEquals(10_000, faturaRepo.parcelas.sumOf { it.valorCentavos })
        assertEquals(3, faturaRepo.faturas.size)
    }

    @Test
    fun compraBloqueadaQuandoExcedeLimite() = runTest {
        val cartaoRepo = FakeCartaoRepository(
            Cartao(limiteCentavos = 10_000, diaFechamento = 10, diaVencimento = 17),
        )
        cartaoRepo.parcelasSoma = 9_000
        val faturaRepo = FakeFaturaRepository()
        val useCase = RegistrarCompraCartaoUseCase(cartaoRepo, faturaRepo)

        assertFailsWith<IllegalStateException> {
            useCase(
                descricao = "Grande",
                valorCentavos = 5_000,
                categoria = ECategoriaGasto.OUTROS,
                data = System.currentTimeMillis(),
                tipo = ETipoCompraCartao.CREDITO_AVISTA,
            )
        }
    }

    @Test
    fun pagamentoParcialMantemAbertaEIntegralMarcaPaga() = runTest {
        val faturaRepo = FakeFaturaRepository()
        val pagRepo = FakePagamentoRepo()
        val fatura = Fatura(
            id = 1,
            cartaoId = 1,
            mesReferencia = "2026-07",
            dataFechamento = 0,
            dataVencimento = 0,
        )
        faturaRepo.faturas += fatura
        faturaRepo.parcelas += ParcelaCartao(1, 1, 1, 1, 10_000)

        val pagar = PagarFaturaUseCase(faturaRepo, pagRepo)
        pagar(1, 4_000)
        assertEquals(EStatusFatura.ABERTA, faturaRepo.faturas.first().status)
        assertEquals(4_000, faturaRepo.faturas.first().valorPagoCentavos)

        pagar(1, 6_000)
        assertEquals(EStatusFatura.PAGA, faturaRepo.faturas.first().status)
        assertEquals(10_000, faturaRepo.faturas.first().valorPagoCentavos)
    }

    @Test
    fun pagamentoNaoPodeExcederPendente() = runTest {
        val faturaRepo = FakeFaturaRepository()
        val pagRepo = FakePagamentoRepo()
        faturaRepo.faturas += Fatura(
            id = 1, cartaoId = 1, mesReferencia = "2026-07",
            dataFechamento = 0, dataVencimento = 0,
        )
        faturaRepo.parcelas += ParcelaCartao(1, 1, 1, 1, 5_000)

        assertFailsWith<IllegalArgumentException> {
            PagarFaturaUseCase(faturaRepo, pagRepo)(1, 6_000)
        }
    }

    @Test
    fun estornarPagamentoReabreFatura() = runTest {
        val faturaRepo = FakeFaturaRepository()
        val pagRepo = FakePagamentoRepo()
        faturaRepo.faturas += Fatura(
            id = 1, cartaoId = 1, mesReferencia = "2026-07",
            dataFechamento = 0, dataVencimento = 0,
            valorPagoCentavos = 10_000,
            status = EStatusFatura.PAGA,
        )
        faturaRepo.parcelas += ParcelaCartao(1, 1, 1, 1, 10_000)
        pagRepo.pagamentos += PagamentoFatura(id = 1, faturaId = 1, valorCentavos = 10_000, data = 0)

        EstornarPagamentoUseCase(faturaRepo, pagRepo)(1)
        assertTrue(pagRepo.pagamentos.first().estornado)
        assertEquals(EStatusFatura.ABERTA, faturaRepo.faturas.first().status)
        assertEquals(0, faturaRepo.faturas.first().valorPagoCentavos)
    }

    @Test
    fun cancelarRecorrenciaPreservaHistorico() = runTest {
        val faturaRepo = FakeFaturaRepository()
        faturaRepo.recorrencias += RecorrenciaCartao(
            id = 1,
            cartaoId = 1,
            descricao = "Netflix",
            valorCentavos = 5_000,
            categoria = ECategoriaGasto.LAZER,
            diaCobranca = 5,
            ativa = true,
            dataInicio = System.currentTimeMillis(),
        )
        CancelarRecorrenciaCartaoUseCase(faturaRepo)(1)
        assertEquals(false, faturaRepo.recorrencias.first().ativa)
        assertTrue(faturaRepo.recorrencias.first().dataCancelamento != null)
    }
}
