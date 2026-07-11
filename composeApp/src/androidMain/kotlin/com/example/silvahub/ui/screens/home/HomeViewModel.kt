package com.example.silvahub.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.FaturaDetalhe
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.model.OrcamentoComProgresso
import com.example.silvahub.domain.model.ResumoFinanceiro
import com.example.silvahub.domain.usecase.ObterFaturaAtualUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.domain.usecase.ObterResumoFinanceiroUseCase
import com.example.silvahub.domain.usecase.ObterUltimosGastosUseCase
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val resumo: ResumoFinanceiro? = null,
    val ultimosGastos: List<Gasto> = emptyList(),
    val orcamentos: List<OrcamentoComProgresso> = emptyList(),
    val faturaAtual: FaturaDetalhe? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
)

class HomeViewModel(
    private val obterResumoFinanceiroUseCase: ObterResumoFinanceiroUseCase,
    private val obterUltimosGastosUseCase: ObterUltimosGastosUseCase,
    private val obterOrcamentosComProgressoUseCase: ObterOrcamentosComProgressoUseCase,
    private val obterFaturaAtualUseCase: ObterFaturaAtualUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val mes = DateUtils.mesReferenciaAtual()

    init {
        observar()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        _uiState.update { it.copy(isRefreshing = false) }
    }

    private fun observar() {
        viewModelScope.launch {
            obterResumoFinanceiroUseCase(mes).collect { resumo ->
                _uiState.update { it.copy(resumo = resumo, isLoading = false) }
            }
        }
        viewModelScope.launch {
            obterUltimosGastosUseCase(5).collect { gastos ->
                _uiState.update { it.copy(ultimosGastos = gastos) }
            }
        }
        viewModelScope.launch {
            obterOrcamentosComProgressoUseCase(mes).collect { list ->
                _uiState.update { it.copy(orcamentos = list) }
            }
        }
        viewModelScope.launch {
            obterFaturaAtualUseCase().collect { fatura ->
                _uiState.update { it.copy(faturaAtual = fatura) }
            }
        }
    }
}
