package com.example.silvahub.ui.screens.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.model.InsightFinanceiro
import com.example.silvahub.domain.usecase.ObterGastoDoMesUseCase
import com.example.silvahub.domain.usecase.ObterInsightsUseCase
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
    val gastos: List<Gasto> = emptyList(),
    val insights: List<InsightFinanceiro> = emptyList(),
    val filtroCategoria: ECategoriaGasto? = null,
    val total: Double = 0.0,
)

class HistoricoViewModel(
    private val obterGastoDoMesUseCase: ObterGastoDoMesUseCase,
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
        _uiState.update { state ->
            val filtrados = if (categoria == null) {
                state.gastos
            } else {
                // re-filter from full list via observing - store raw
                state.gastos
            }
            state.copy(filtroCategoria = categoria)
        }
        // Force refresh display by re-collecting isn't needed; screen filters
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observar() {
        viewModelScope.launch {
            mesFlow.flatMapLatest { mes -> obterGastoDoMesUseCase(mes) }.collect { gastos ->
                _uiState.update {
                    it.copy(gastos = gastos, total = gastos.sumOf { g -> g.valor })
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
