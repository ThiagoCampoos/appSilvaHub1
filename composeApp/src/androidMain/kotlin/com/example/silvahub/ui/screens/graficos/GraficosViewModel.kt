package com.example.silvahub.ui.screens.graficos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.label
import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriaValor(val label: String, val valor: Double)
data class MesValor(val mes: String, val valor: Double)

data class GraficosUiState(
    val porCategoria: List<CategoriaValor> = emptyList(),
    val evolucaoMensal: List<MesValor> = emptyList(),
)

class GraficosViewModel(
    private val gastoRepository: GastoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraficosUiState())
    val uiState: StateFlow<GraficosUiState> = _uiState.asStateFlow()

    init {
        observar()
    }

    private fun observar() {
        val mesAtual = DateUtils.mesReferenciaAtual()
        viewModelScope.launch {
            val catFlows = ECategoriaGasto.entries.map { cat ->
                gastoRepository.getTotalPorCategoriaNoMes(cat, mesAtual)
            }
            combine(catFlows) { valores ->
                ECategoriaGasto.entries.zip(valores.toList()).map { (cat, valor) ->
                    CategoriaValor(cat.label(), valor)
                }.filter { it.valor > 0 }
            }.collect { list ->
                _uiState.update { it.copy(porCategoria = list) }
            }
        }

        viewModelScope.launch {
            val meses = (5 downTo 0).map { offset ->
                var mes = mesAtual
                repeat(offset) { mes = DateUtils.previousMesAno(mes) }
                // Actually we want last 6 months ending at current
                mes
            }.let {
                // rebuild properly
                val result = mutableListOf<String>()
                var m = mesAtual
                result.add(m)
                repeat(5) {
                    m = DateUtils.previousMesAno(m)
                    result.add(m)
                }
                result.reversed()
            }

            val flows = meses.map { gastoRepository.getTotalDoMes(it) }
            combine(flows) { valores ->
                meses.zip(valores.toList()).map { (mes, valor) -> MesValor(mes, valor) }
            }.collect { list ->
                _uiState.update { it.copy(evolucaoMensal = list) }
            }
        }
    }
}
