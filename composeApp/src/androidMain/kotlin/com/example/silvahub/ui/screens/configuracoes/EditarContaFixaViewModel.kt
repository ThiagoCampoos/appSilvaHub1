package com.example.silvahub.ui.screens.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.usecase.AtualizarContaFixaUseCase
import com.example.silvahub.domain.usecase.ObterContaFixaPorIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditarContaFixaUiState(
    val nome: String = "",
    val valor: String = "",
    val dia: String = "",
    val ativa: Boolean = true,
    val contaId: Long = 0,
    val saved: Boolean = false,
    val errorMessage: String? = null,
)

class EditarContaFixaViewModel(
    private val contaId: Long,
    private val obterContaFixaPorIdUseCase: ObterContaFixaPorIdUseCase,
    private val atualizarContaFixaUseCase: AtualizarContaFixaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarContaFixaUiState(contaId = contaId))
    val uiState: StateFlow<EditarContaFixaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obterContaFixaPorIdUseCase(contaId).collect { conta ->
                if (conta != null) {
                    _uiState.update {
                        it.copy(
                            nome = conta.nome,
                            valor = conta.valor.toString(),
                            dia = conta.diaVencimento.toString(),
                            ativa = conta.ativa,
                            contaId = conta.id,
                        )
                    }
                }
            }
        }
    }

    fun onNomeChange(v: String) = _uiState.update { it.copy(nome = v) }
    fun onValorChange(v: String) = _uiState.update { it.copy(valor = v.filter { ch -> ch.isDigit() || ch == '.' }) }
    fun onDiaChange(v: String) = _uiState.update { it.copy(dia = v.filter { ch -> ch.isDigit() }.take(2)) }
    fun onAtivaChange(v: Boolean) = _uiState.update { it.copy(ativa = v) }

    fun salvar() {
        val state = uiState.value
        val valor = state.valor.toDoubleOrNull()
        val dia = state.dia.toIntOrNull()
        viewModelScope.launch {
            runCatching {
                atualizarContaFixaUseCase(
                    ContaFixa(
                        id = state.contaId,
                        nome = state.nome.trim(),
                        valor = valor ?: error("Valor inválido"),
                        diaVencimento = dia ?: error("Dia inválido"),
                        ativa = state.ativa,
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(saved = true) }
            }.onFailure { t ->
                _uiState.update { it.copy(errorMessage = t.message) }
            }
        }
    }
}
