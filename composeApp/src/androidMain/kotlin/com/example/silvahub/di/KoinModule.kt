package com.example.silvahub.di

import com.example.silvahub.data.backup.BackupRepository
import com.example.silvahub.data.local.database.AppDatabase
import com.example.silvahub.data.preferences.UserPreferencesRepository
import com.example.silvahub.data.repository.ContaFixaRepositoryImpl
import com.example.silvahub.data.repository.GastoRepositoryImpl
import com.example.silvahub.data.repository.OrcamentoRepositoryImpl
import com.example.silvahub.data.repository.SalarioExtraRepositoryImpl
import com.example.silvahub.data.repository.SalarioRepositoryImpl
import com.example.silvahub.domain.repository.ContaFixaRepository
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.domain.repository.OrcamentoRepository
import com.example.silvahub.domain.repository.SalarioExtraRepository
import com.example.silvahub.domain.repository.SalarioRepository
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoParceladoUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoRecorrenteUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoUseCase
import com.example.silvahub.domain.usecase.AdicionarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.AtualizarContaFixaUseCase
import com.example.silvahub.domain.usecase.DefinirOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarGastoUseCase
import com.example.silvahub.domain.usecase.DeletarOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.ExportarBackupUseCase
import com.example.silvahub.domain.usecase.ExportarGastosCsvUseCase
import com.example.silvahub.domain.usecase.ImportarBackupUseCase
import com.example.silvahub.domain.usecase.ObterContaFixaPorIdUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterGastoDoMesUseCase
import com.example.silvahub.domain.usecase.ObterGastoPorIdUseCase
import com.example.silvahub.domain.usecase.ObterInsightsUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.domain.usecase.ObterResumoFinanceiroUseCase
import com.example.silvahub.domain.usecase.ObterSalarioDoMesUseCase
import com.example.silvahub.domain.usecase.ObterSalariosExtrasUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.ObterUltimosGastosUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
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

    single { UserPreferencesRepository(androidContext()) }
    single {
        BackupRepository(
            salarioDao = get(),
            contaFixaDao = get(),
            gastoDao = get(),
            salarioExtraDao = get(),
            orcamentoDao = get(),
        )
    }

    single<SalarioRepository> { SalarioRepositoryImpl(get()) }
    single<ContaFixaRepository> { ContaFixaRepositoryImpl(get()) }
    single<GastoRepository> { GastoRepositoryImpl(get()) }
    single<SalarioExtraRepository> { SalarioExtraRepositoryImpl(get()) }
    single<OrcamentoRepository> { OrcamentoRepositoryImpl(get()) }

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
    factory { ObterResumoFinanceiroUseCase(get(), get(), get(), get()) }

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
    viewModel { GastosViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { HistoricoViewModel(get(), get()) }
    viewModel { GraficosViewModel(get()) }
    viewModel { (gastoId: Long) ->
        com.example.silvahub.ui.screens.gastos.DetalhesGastoViewModel(gastoId, get(), get())
    }
    viewModel { (contaId: Long) ->
        com.example.silvahub.ui.screens.configuracoes.EditarContaFixaViewModel(contaId, get(), get())
    }
}
