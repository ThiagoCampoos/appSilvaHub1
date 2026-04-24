package com.example.silvahub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
import com.example.silvahub.ui.screens.configuracoes.ConfiguracoesScreen
import com.example.silvahub.ui.screens.configuracoes.ConfiguracoesViewModel
import org.koin.java.KoinJavaComponent.get


@Composable
fun AppNavHost() {
    val viewModel = remember {
        ConfiguracoesViewModel(
            salvarSalarioUseCase = get(SalvarSalarioUseCase::class.java),
            obterUltimoSalarioUseCase = get(ObterUltimoSalarioUseCase::class.java),
            adicionarContaFixaUseCase = get(AdicionarContaFixaUseCase::class.java),
            obterContasFixasUseCase = get(ObterContasFixasUseCase::class.java),
            deletarContaFixaUseCase = get(DeletarContaFixaUseCase::class.java),
        )
    }

    ConfiguracoesScreen(viewModel = viewModel)
}