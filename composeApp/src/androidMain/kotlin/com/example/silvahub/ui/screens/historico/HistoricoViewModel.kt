package com.example.silvahub.ui.screens.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.InsightFinanceiro
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.usecase.ObterInsightsUseCase
import com.example.silvahub.domain.usecase.ObterLancamentosDoMesUseCase
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoricoUiState(
    val mesReferencia: String = DateUtils.mesReferenciaAtual(),
    val lancamentos: List<Lancamento> = emptyList(),
    val insights: List<InsightFinanceiro> = emptyList(),
    val filtroCategoria: ECategoriaGasto? = null,
    val total: Double = 0.0,
)

class HistoricoViewModel(
    private val obterLancamentosDoMesUseCase: ObterLancamentosDoMesUseCase,
    private val obterInsightsUseCase: ObterInsightsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricoUiState())
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    private val mesFlow = MutableStateFlow(DateUtils.mesReferenciaAtual())

    init {
        observar()
    }

    fun mesAnterior() {
        val novo = DateUtils.previousMesAno(uiState.value.mesReferencia)
        mesFlow.value = novo
        _uiState.update { it.copy(mesReferencia = novo) }
    }

    fun mesProximo() {
        val novo = DateUtils.nextMesAno(uiState.value.mesReferencia)
        mesFlow.value = novo
        _uiState.update { it.copy(mesReferencia = novo) }
    }

    fun filtrarCategoria(categoria: ECategoriaGasto?) {
        _uiState.update { it.copy(filtroCategoria = categoria) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observar() {
        viewModelScope.launch {
            mesFlow.flatMapLatest { mes -> obterLancamentosDoMesUseCase(mes) }.collect { list ->
                _uiState.update {
                    it.copy(lancamentos = list, total = list.sumOf { l -> l.valor })
                }
            }
        }
        viewModelScope.launch {
            mesFlow.flatMapLatest { mes -> obterInsightsUseCase(mes) }.collect { insights ->
                _uiState.update { it.copy(insights = insights) }
            }
        }
    }
}
