package com.example.silvahub.ui.screens.cartao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.usecase.AnteciparParcelasUseCase
import com.example.silvahub.domain.usecase.CancelarRecorrenciaCartaoUseCase
import com.example.silvahub.domain.usecase.DeletarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.EditarCompraCartaoUseCase
import com.example.silvahub.domain.usecase.EstornarCompraCartaoUseCase
import com.example.silvahub.util.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalhesCompraCartaoUiState(
    val compra: CompraCartao? = null,
    val parcelas: List<ParcelaCartao> = emptyList(),
    val descricaoInput: String = "",
    val valorInput: String = "",
    val categoria: ECategoriaGasto = ECategoriaGasto.OUTROS,
    val parcelasSelecionadas: Set<Int> = emptySet(),
    val deleted: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

class DetalhesCompraCartaoViewModel(
    private val compraId: Long,
    private val faturaRepository: FaturaRepository,
    private val editarCompraCartaoUseCase: EditarCompraCartaoUseCase,
    private val deletarCompraCartaoUseCase: DeletarCompraCartaoUseCase,
    private val estornarCompraCartaoUseCase: EstornarCompraCartaoUseCase,
    private val anteciparParcelasUseCase: AnteciparParcelasUseCase,
    private val cancelarRecorrenciaCartaoUseCase: CancelarRecorrenciaCartaoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalhesCompraCartaoUiState())
    val uiState: StateFlow<DetalhesCompraCartaoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            faturaRepository.getCompraPorId(compraId).collect { compra ->
                if (compra != null) {
                    _uiState.update {
                        it.copy(
                            compra = compra,
                            descricaoInput = compra.descricao,
                            valorInput = Money.fromCentavos(compra.valorTotalCentavos).toString(),
                            categoria = compra.categoria,
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            faturaRepository.getParcelasDaCompraFlow(compraId).collect { parcelas ->
                _uiState.update { it.copy(parcelas = parcelas) }
            }
        }
    }

    fun onDescricaoChange(v: String) = _uiState.update { it.copy(descricaoInput = v) }
    fun onValorChange(v: String) = _uiState.update {
        it.copy(valorInput = v.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
            .replace(',', '.'))
    }
    fun onCategoriaChange(c: ECategoriaGasto) = _uiState.update { it.copy(categoria = c) }
    fun toggleParcela(num: Int) = _uiState.update {
        val set = it.parcelasSelecionadas.toMutableSet()
        if (!set.add(num)) set.remove(num)
        it.copy(parcelasSelecionadas = set)
    }

    fun limparMensagens() = _uiState.update {
        it.copy(errorMessage = null, successMessage = null)
    }

    fun salvarEdicao() {
        val state = uiState.value
        val valor = Money.parseInputToCentavos(state.valorInput)
        viewModelScope.launch {
            runCatching {
                editarCompraCartaoUseCase(
                    compraId = compraId,
                    descricao = state.descricaoInput.trim(),
                    categoria = state.categoria,
                    novoValorCentavos = valor,
                )
            }.onSuccess {
                _uiState.update { it.copy(successMessage = "Compra atualizada") }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun excluir() {
        viewModelScope.launch {
            runCatching { deletarCompraCartaoUseCase(compraId) }
                .onSuccess { _uiState.update { it.copy(deleted = true) } }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun estornar() {
        viewModelScope.launch {
            runCatching { estornarCompraCartaoUseCase(compraId) }
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Compra estornada") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun anteciparSelecionadas() {
        val nums = uiState.value.parcelasSelecionadas.toList()
        if (nums.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Selecione parcelas para antecipar") }
            return
        }
        viewModelScope.launch {
            runCatching { anteciparParcelasUseCase(compraId, nums) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            successMessage = "Parcelas antecipadas",
                            parcelasSelecionadas = emptySet(),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun cancelarRecorrencia() {
        val recId = uiState.value.compra?.recorrenciaId ?: return
        viewModelScope.launch {
            runCatching { cancelarRecorrenciaCartaoUseCase(recId) }
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Recorrência cancelada") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }
}
