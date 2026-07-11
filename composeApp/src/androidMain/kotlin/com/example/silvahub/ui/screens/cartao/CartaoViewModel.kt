package com.example.silvahub.ui.screens.cartao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.FaturaDetalhe
import com.example.silvahub.domain.model.RecorrenciaCartao
import com.example.silvahub.domain.model.ResumoLimite
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.usecase.CancelarRecorrenciaCartaoUseCase
import com.example.silvahub.domain.usecase.ObterCartaoUseCase
import com.example.silvahub.domain.usecase.ObterFaturasUseCase
import com.example.silvahub.domain.usecase.ObterResumoLimiteUseCase
import com.example.silvahub.domain.usecase.SalvarCartaoUseCase
import com.example.silvahub.util.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartaoUiState(
    val cartao: Cartao? = null,
    val resumoLimite: ResumoLimite? = null,
    val faturas: List<FaturaDetalhe> = emptyList(),
    val recorrencias: List<RecorrenciaCartao> = emptyList(),
    val limiteInput: String = "",
    val diaFechamentoInput: String = "10",
    val diaVencimentoInput: String = "17",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val avisoLimite: String? = null,
)

class CartaoViewModel(
    private val obterCartaoUseCase: ObterCartaoUseCase,
    private val salvarCartaoUseCase: SalvarCartaoUseCase,
    private val obterResumoLimiteUseCase: ObterResumoLimiteUseCase,
    private val obterFaturasUseCase: ObterFaturasUseCase,
    private val faturaRepository: FaturaRepository,
    private val cancelarRecorrenciaCartaoUseCase: CancelarRecorrenciaCartaoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartaoUiState())
    val uiState: StateFlow<CartaoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obterCartaoUseCase().collect { cartao ->
                _uiState.update {
                    it.copy(
                        cartao = cartao,
                        limiteInput = cartao?.let {
                            Money.fromCentavos(it.limiteCentavos).toString()
                        } ?: it.limiteInput,
                        diaFechamentoInput = cartao?.diaFechamento?.toString()
                            ?: it.diaFechamentoInput,
                        diaVencimentoInput = cartao?.diaVencimento?.toString()
                            ?: it.diaVencimentoInput,
                    )
                }
            }
        }
        viewModelScope.launch {
            obterResumoLimiteUseCase().collect { resumo ->
                _uiState.update { it.copy(resumoLimite = resumo) }
            }
        }
        viewModelScope.launch {
            obterFaturasUseCase().collect { faturas ->
                _uiState.update { it.copy(faturas = faturas) }
            }
        }
        viewModelScope.launch {
            faturaRepository.getRecorrenciasAtivas(Cartao.CARTAO_UNICO_ID).collect { list ->
                _uiState.update { it.copy(recorrencias = list) }
            }
        }
    }

    fun onLimiteChange(v: String) = _uiState.update { it.copy(limiteInput = v.filterMoney()) }
    fun onFechamentoChange(v: String) = _uiState.update {
        it.copy(diaFechamentoInput = v.filter { ch -> ch.isDigit() }.take(2))
    }
    fun onVencimentoChange(v: String) = _uiState.update {
        it.copy(diaVencimentoInput = v.filter { ch -> ch.isDigit() }.take(2))
    }

    fun limparMensagens() = _uiState.update {
        it.copy(errorMessage = null, successMessage = null, avisoLimite = null)
    }

    fun salvar() {
        val state = uiState.value
        val limite = Money.parseInputToCentavos(state.limiteInput)
        val fechamento = state.diaFechamentoInput.toIntOrNull()
        val vencimento = state.diaVencimentoInput.toIntOrNull()
        if (limite == null || limite <= 0) {
            _uiState.update { it.copy(errorMessage = "Informe um limite válido") }
            return
        }
        if (fechamento == null || vencimento == null) {
            _uiState.update { it.copy(errorMessage = "Informe dias de fechamento e vencimento") }
            return
        }
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }
                salvarCartaoUseCase(limite, fechamento, vencimento)
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Cartão salvo",
                        avisoLimite = if (result.avisoLimiteAbaixoUtilizado) {
                            "Atenção: o novo limite está abaixo do valor já utilizado. " +
                                "Novas compras no crédito ficam bloqueadas até liberar limite."
                        } else {
                            null
                        },
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Erro ao salvar")
                }
            }
        }
    }

    fun cancelarRecorrencia(id: Long) {
        viewModelScope.launch {
            runCatching { cancelarRecorrenciaCartaoUseCase(id) }
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Recorrência cancelada") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    private fun String.filterMoney(): String {
        val filtered = filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        val firstDot = filtered.indexOf('.')
        return if (firstDot == -1) {
            filtered
        } else {
            filtered.substring(0, firstDot + 1) +
                filtered.substring(firstDot + 1).replace(".", "")
        }
    }
}
