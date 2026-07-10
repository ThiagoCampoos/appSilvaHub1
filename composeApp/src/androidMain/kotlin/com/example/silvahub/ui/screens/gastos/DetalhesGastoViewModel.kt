package com.example.silvahub.ui.screens.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.usecase.DeletarGastoUseCase
import com.example.silvahub.domain.usecase.ObterGastoPorIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalhesGastoUiState(
    val gasto: Gasto? = null,
    val deleted: Boolean = false,
    val errorMessage: String? = null,
)

class DetalhesGastoViewModel(
    private val gastoId: Long,
    private val obterGastoPorIdUseCase: ObterGastoPorIdUseCase,
    private val deletarGastoUseCase: DeletarGastoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalhesGastoUiState())
    val uiState: StateFlow<DetalhesGastoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obterGastoPorIdUseCase(gastoId).collect { gasto ->
                _uiState.update { it.copy(gasto = gasto) }
            }
        }
    }

    fun deletar(excluirRestantes: Boolean = false) {
        viewModelScope.launch {
            runCatching { deletarGastoUseCase(gastoId, excluirRestantes) }
                .onSuccess { _uiState.update { it.copy(deleted = true) } }
                .onFailure { t -> _uiState.update { it.copy(errorMessage = t.message) } }
        }
    }
}
