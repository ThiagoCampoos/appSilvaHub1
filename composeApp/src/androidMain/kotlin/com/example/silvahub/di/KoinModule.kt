package com.example.silvahub.di

import com.example.silvahub.data.backup.BackupRepository
import com.example.silvahub.data.local.database.AppDatabase
import com.example.silvahub.data.preferences.UserPreferencesRepository
import com.example.silvahub.data.repository.CartaoRepositoryImpl
import com.example.silvahub.data.repository.ContaFixaRepositoryImpl
import com.example.silvahub.data.repository.FaturaRepositoryImpl
import com.example.silvahub.data.repository.GastoRepositoryImpl
import com.example.silvahub.data.repository.OrcamentoRepositoryImpl
import com.example.silvahub.data.repository.PagamentoFaturaRepositoryImpl
import com.example.silvahub.data.repository.SalarioExtraRepositoryImpl
import com.example.silvahub.data.repository.SalarioRepositoryImpl
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.ContaFixaRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.domain.repository.OrcamentoRepository
import com.example.silvahub.domain.repository.PagamentoFaturaRepository
import com.example.silvahub.domain.repository.SalarioExtraRepository
import com.example.silvahub.domain.repository.SalarioRepository
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoParceladoUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoRecorrenteUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoUseCase
import com.example.silvahub.domain.usecase.AdicionarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.AnteciparParcelasUseCase
import com.example.silvahub.domain.usecase.AtualizarContaFixaUseCase
import com.example.silvahub.domain.usecase.CancelarRecorrenciaCartaoUseCase
import com.example.silvahub.domain.usecase.CriarRecorrenciaCartaoUseCase
import com.example.silvahub.domain.usecase.DefinirOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarGastoUseCase
import com.example.silvahub.domain.usecase.DeletarOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.EditarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.EstornarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.EstornarPagamentoUseCase
import com.example.silvahub.domain.usecase.ExportarBackupUseCase
import com.example.silvahub.domain.usecase.ExportarGastosCsvUseCase
import com.example.silvahub.domain.usecase.GerarCobrancasRecorrentesUseCase
import com.example.silvahub.domain.usecase.ImportarBackupUseCase
import com.example.silvahub.domain.usecase.ObterCartaoUseCase
import com.example.silvahub.domain.usecase.ObterContaFixaPorIdUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterDetalhesFaturaUseCase
import com.example.silvahub.domain.usecase.ObterFaturaAtualUseCase
import com.example.silvahub.domain.usecase.ObterFaturasUseCase
import com.example.silvahub.domain.usecase.ObterGastoDoMesUseCase
import com.example.silvahub.domain.usecase.ObterGastoPorIdUseCase
import com.example.silvahub.domain.usecase.ObterInsightsUseCase
import com.example.silvahub.domain.usecase.ObterLancamentosDoMesUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.domain.usecase.ObterResumoFinanceiroUseCase
import com.example.silvahub.domain.usecase.ObterResumoLimiteUseCase
import com.example.silvahub.domain.usecase.ObterSalarioDoMesUseCase
import com.example.silvahub.domain.usecase.ObterSalariosExtrasUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.ObterUltimosGastosUseCase
import com.example.silvahub.domain.usecase.PagarFaturaUseCase
import com.example.silvahub.domain.usecase.RegistrarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.SalvarCartaoUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
import com.example.silvahub.ui.screens.cartao.CartaoViewModel
import com.example.silvahub.ui.screens.cartao.DetalhesCompraCartaoViewModel
import com.example.silvahub.ui.screens.cartao.DetalhesFaturaViewModel
import com.example.silvahub.ui.screens.configuracoes.ConfiguracoesViewModel
import com.example.silvahub.ui.screens.gastos.GastosViewModel
import com.example.silvahub.ui.screens.graficos.GraficosViewModel
import com.example.silvahub.ui.screens.historico.HistoricoViewModel
import com.example.silvahub.ui.screens.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().contaFixaDao() }
    single { get<AppDatabase>().salarioDao() }
    single { get<AppDatabase>().gastoDao() }
    single { get<AppDatabase>().salarioExtraDao() }
    single { get<AppDatabase>().orcamentoDao() }
    single { get<AppDatabase>().cartaoDao() }
    single { get<AppDatabase>().faturaDao() }
    single { get<AppDatabase>().pagamentoFaturaDao() }

    single { UserPreferencesRepository(androidContext()) }
    single {
        BackupRepository(
            database = get(),
            salarioDao = get(),
            contaFixaDao = get(),
            gastoDao = get(),
            salarioExtraDao = get(),
            orcamentoDao = get(),
            cartaoDao = get(),
            faturaDao = get(),
            pagamentoFaturaDao = get(),
        )
    }

    single<SalarioRepository> { SalarioRepositoryImpl(get()) }
    single<ContaFixaRepository> { ContaFixaRepositoryImpl(get()) }
    single<GastoRepository> { GastoRepositoryImpl(get()) }
    single<SalarioExtraRepository> { SalarioExtraRepositoryImpl(get()) }
    single<OrcamentoRepository> { OrcamentoRepositoryImpl(get()) }
    single<CartaoRepository> { CartaoRepositoryImpl(get(), get(), get()) }
    single<FaturaRepository> { FaturaRepositoryImpl(get(), get()) }
    single<PagamentoFaturaRepository> { PagamentoFaturaRepositoryImpl(get()) }

    factory { SalvarSalarioUseCase(get()) }
    factory { ObterSalarioDoMesUseCase(get()) }
    factory { ObterUltimoSalarioUseCase(get()) }
    factory { AdicionarContaFixaUseCase(get()) }
    factory { ObterContasFixasUseCase(get()) }
    factory { DeletarContaFixaUseCase(get()) }
    factory { AtualizarContaFixaUseCase(get()) }
    factory { ObterContaFixaPorIdUseCase(get()) }

    factory { AdicionarGastoUseCase(get()) }
    factory { AdicionarGastoParceladoUseCase(get()) }
    factory { AdicionarGastoRecorrenteUseCase(get()) }
    factory { DeletarGastoUseCase(get()) }
    factory { ObterGastoDoMesUseCase(get()) }
    factory { ObterUltimosGastosUseCase(get()) }
    factory { ObterGastoPorIdUseCase(get()) }

    factory { AdicionarSalarioExtraUseCase(get()) }
    factory { ObterSalariosExtrasUseCase(get()) }
    factory { DeletarSalarioExtraUseCase(get()) }

    factory { DefinirOrcamentoUseCase(get()) }
    factory { DeletarOrcamentoUseCase(get()) }
    factory { ObterOrcamentosComProgressoUseCase(get(), get()) }
    factory { ObterInsightsUseCase(get()) }
    factory { ObterResumoFinanceiroUseCase(get(), get(), get(), get(), get()) }

    factory { SalvarCartaoUseCase(get()) }
    factory { ObterCartaoUseCase(get()) }
    factory { ObterResumoLimiteUseCase(get()) }
    factory { RegistrarCompraCartaoUseCase(get(), get()) }
    factory { ObterFaturaAtualUseCase(get(), get()) }
    factory { ObterFaturasUseCase(get(), get()) }
    factory { ObterDetalhesFaturaUseCase(get(), get()) }
    factory { PagarFaturaUseCase(get(), get()) }
    factory { EstornarPagamentoUseCase(get(), get()) }
    factory { CriarRecorrenciaCartaoUseCase(get(), get(), get()) }
    factory { CancelarRecorrenciaCartaoUseCase(get()) }
    factory { GerarCobrancasRecorrentesUseCase(get(), get(), get()) }
    factory { EditarCompraCartaoUseCase(get(), get()) }
    factory { DeletarCompraCartaoUseCase(get()) }
    factory { EstornarCompraCartaoUseCase(get(), get(), get()) }
    factory { AnteciparParcelasUseCase(get(), get()) }
    factory { ObterLancamentosDoMesUseCase(get(), get(), get()) }

    factory { ExportarBackupUseCase(get()) }
    factory { ImportarBackupUseCase(get()) }
    factory { ExportarGastosCsvUseCase(get()) }

    viewModel {
        ConfiguracoesViewModel(
            salvarSalarioUseCase = get(),
            obterUltimoSalarioUseCase = get(),
            adicionarContaFixaUseCase = get(),
            obterContasFixasUseCase = get(),
            deletarContaFixaUseCase = get(),
            adicionarSalarioExtraUseCase = get(),
            obterSalariosExtrasUseCase = get(),
            deletarSalarioExtraUseCase = get(),
            definirOrcamentoUseCase = get(),
            deletarOrcamentoUseCase = get(),
            obterOrcamentosComProgressoUseCase = get(),
            exportarBackupUseCase = get(),
            importarBackupUseCase = get(),
            preferencesRepository = get(),
        )
    }
    viewModel {
        GastosViewModel(
            obterLancamentosDoMesUseCase = get(),
            adicionarGastoUseCase = get(),
            registrarCompraCartaoUseCase = get(),
            criarRecorrenciaCartaoUseCase = get(),
            deletarGastoUseCase = get(),
            obterOrcamentosComProgressoUseCase = get(),
            obterCartaoUseCase = get(),
        )
    }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { HistoricoViewModel(get(), get()) }
    viewModel { GraficosViewModel(get()) }
    viewModel { (gastoId: Long) ->
        com.example.silvahub.ui.screens.gastos.DetalhesGastoViewModel(gastoId, get(), get())
    }
    viewModel { (contaId: Long) ->
        com.example.silvahub.ui.screens.configuracoes.EditarContaFixaViewModel(contaId, get(), get())
    }
    viewModel {
        CartaoViewModel(
            obterCartaoUseCase = get(),
            salvarCartaoUseCase = get(),
            obterResumoLimiteUseCase = get(),
            obterFaturasUseCase = get(),
            faturaRepository = get(),
            cancelarRecorrenciaCartaoUseCase = get(),
        )
    }
    viewModel { (faturaId: Long) ->
        DetalhesFaturaViewModel(
            faturaId = faturaId,
            obterDetalhesFaturaUseCase = get(),
            pagarFaturaUseCase = get(),
            estornarPagamentoUseCase = get(),
        )
    }
    viewModel { (compraId: Long) ->
        DetalhesCompraCartaoViewModel(
            compraId = compraId,
            faturaRepository = get(),
            editarCompraCartaoUseCase = get(),
            deletarCompraCartaoUseCase = get(),
            estornarCompraCartaoUseCase = get(),
            anteciparParcelasUseCase = get(),
            cancelarRecorrenciaCartaoUseCase = get(),
        )
    }
}
