package com.example.silvahub.ui.screens.cartao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.FaturaDetalhe
import com.example.silvahub.domain.usecase.EstornarPagamentoUseCase
import com.example.silvahub.domain.usecase.ObterDetalhesFaturaUseCase
import com.example.silvahub.domain.usecase.PagarFaturaUseCase
import com.example.silvahub.util.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalhesFaturaUiState(
    val detalhe: FaturaDetalhe? = null,
    val valorPagamentoInput: String = "",
    val showPagamentoDialog: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

class DetalhesFaturaViewModel(
    private val faturaId: Long,
    private val obterDetalhesFaturaUseCase: ObterDetalhesFaturaUseCase,
    private val pagarFaturaUseCase: PagarFaturaUseCase,
    private val estornarPagamentoUseCase: EstornarPagamentoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalhesFaturaUiState())
    val uiState: StateFlow<DetalhesFaturaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obterDetalhesFaturaUseCase(faturaId).collect { detalhe ->
                _uiState.update { it.copy(detalhe = detalhe) }
            }
        }
    }

    fun openPagamento() {
        val pendente = uiState.value.detalhe?.saldoPendenteCentavos ?: 0
        _uiState.update {
            it.copy(
                showPagamentoDialog = true,
                valorPagamentoInput = Money.fromCentavos(pendente).toString(),
            )
        }
    }

    fun closePagamento() = _uiState.update { it.copy(showPagamentoDialog = false) }
    fun onValorPagamentoChange(v: String) = _uiState.update {
        it.copy(valorPagamentoInput = v.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
            .replace(',', '.'))
    }

    fun limparMensagens() = _uiState.update {
        it.copy(errorMessage = null, successMessage = null)
    }

    fun pagar() {
        val valor = Money.parseInputToCentavos(uiState.value.valorPagamentoInput)
        if (valor == null || valor <= 0) {
            _uiState.update { it.copy(errorMessage = "Informe um valor válido") }
            return
        }
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }
                pagarFaturaUseCase(faturaId, valor)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showPagamentoDialog = false,
                        successMessage = "Pagamento registrado",
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Erro no pagamento")
                }
            }
        }
    }

    fun estornarPagamento(pagamentoId: Long) {
        viewModelScope.launch {
            runCatching { estornarPagamentoUseCase(pagamentoId) }
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Pagamento estornado") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }
}
