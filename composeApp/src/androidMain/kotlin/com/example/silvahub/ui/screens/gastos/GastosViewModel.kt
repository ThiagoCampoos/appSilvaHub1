package com.example.silvahub.ui.screens.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.model.OrcamentoComProgresso
import com.example.silvahub.domain.usecase.AdicionarGastoParceladoUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoRecorrenteUseCase
import com.example.silvahub.domain.usecase.AdicionarGastoUseCase
import com.example.silvahub.domain.usecase.DeletarGastoUseCase
import com.example.silvahub.domain.usecase.ObterGastoDoMesUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ModoLancamento { AVISTA, PARCELADO, RECORRENTE }

data class GastosUiState(
    val mesReferencia: String = DateUtils.mesReferenciaAtual(),
    val gastos: List<Gasto> = emptyList(),
    val total: Double = 0.0,
    val orcamentos: List<OrcamentoComProgresso> = emptyList(),
    val showSheet: Boolean = false,
    val descricaoInput: String = "",
    val valorInput: String = "",
    val categoria: ECategoriaGasto = ECategoriaGasto.OUTROS,
    val modo: ModoLancamento = ModoLancamento.AVISTA,
    val parcelasInput: String = "2",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val openSheetFromDeepLink: Boolean = false,
)

class GastosViewModel(
    private val obterGastoDoMesUseCase: ObterGastoDoMesUseCase,
    private val adicionarGastoUseCase: AdicionarGastoUseCase,
    private val adicionarGastoParceladoUseCase: AdicionarGastoParceladoUseCase,
    private val adicionarGastoRecorrenteUseCase: AdicionarGastoRecorrenteUseCase,
    private val deletarGastoUseCase: DeletarGastoUseCase,
    private val obterOrcamentosComProgressoUseCase: ObterOrcamentosComProgressoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastosUiState())
    val uiState: StateFlow<GastosUiState> = _uiState.asStateFlow()

    private var lastDeleted: Gasto? = null

    init {
        observarGastos()
        observarOrcamentos()
    }

    fun openSheet(fromDeepLink: Boolean = false) {
        _uiState.update { it.copy(showSheet = true, openSheetFromDeepLink = fromDeepLink) }
    }

    fun closeSheet() {
        _uiState.update { it.copy(showSheet = false) }
    }

    fun onDescricaoChange(value: String) = _uiState.update { it.copy(descricaoInput = value) }
    fun onValorChange(value: String) = _uiState.update { it.copy(valorInput = value.filterMoney()) }
    fun onCategoriaChange(value: ECategoriaGasto) = _uiState.update { it.copy(categoria = value) }
    fun onModoChange(value: ModoLancamento) = _uiState.update { it.copy(modo = value) }
    fun onParcelasChange(value: String) = _uiState.update {
        it.copy(parcelasInput = value.filter { ch -> ch.isDigit() }.take(2))
    }

    fun limparMensagens() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }

    fun salvarGasto() {
        val state = uiState.value
        val valor = state.valorInput.toDoubleOrNull()
        if (state.descricaoInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe a descrição") }
            return
        }
        if (valor == null || valor <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Informe um valor válido") }
            return
        }

        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                when (state.modo) {
                    ModoLancamento.AVISTA -> {
                        adicionarGastoUseCase(
                            Gasto(
                                descricao = state.descricaoInput.trim(),
                                valor = valor,
                                categoria = state.categoria,
                                data = System.currentTimeMillis(),
                                tipo = ETipoGasto.RAPIDO,
                            ),
                        )
                    }
                    ModoLancamento.PARCELADO -> {
                        val parcelas = state.parcelasInput.toIntOrNull() ?: 2
                        adicionarGastoParceladoUseCase(
                            descricao = state.descricaoInput.trim(),
                            valorParcela = valor,
                            categoria = state.categoria,
                            dataPrimeiraParcela = System.currentTimeMillis(),
                            totalParcelas = parcelas,
                        )
                    }
                    ModoLancamento.RECORRENTE -> {
                        adicionarGastoRecorrenteUseCase(
                            descricao = state.descricaoInput.trim(),
                            valor = valor,
                            categoria = state.categoria,
                            dataInicio = System.currentTimeMillis(),
                        )
                    }
                }
            }.onSuccess {
                val orcamento = uiState.value.orcamentos.find { it.orcamento.categoria == state.categoria }
                val estouro = orcamento != null && (orcamento.gastoAtual + valor) > orcamento.orcamento.limiteMensal
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSheet = false,
                        descricaoInput = "",
                        valorInput = "",
                        modo = ModoLancamento.AVISTA,
                        successMessage = if (estouro) {
                            "Gasto salvo. Atenção: orçamento de ${state.categoria.name} estourado!"
                        } else {
                            "Gasto salvo"
                        },
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao salvar gasto")
                }
            }
        }
    }

    fun deletarGasto(gasto: Gasto, excluirRestantes: Boolean = false) {
        viewModelScope.launch {
            lastDeleted = gasto
            runCatching {
                deletarGastoUseCase(gasto.id, excluirRestantes)
            }.onSuccess {
                _uiState.update { it.copy(successMessage = "Gasto removido") }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Erro ao remover") }
            }
        }
    }

    fun desfazerDelete() {
        val gasto = lastDeleted ?: return
        viewModelScope.launch {
            runCatching {
                adicionarGastoUseCase(gasto.copy(id = 0))
            }
            lastDeleted = null
        }
    }

    private fun observarGastos() {
        viewModelScope.launch {
            obterGastoDoMesUseCase(uiState.value.mesReferencia).collect { gastos ->
                _uiState.update {
                    it.copy(gastos = gastos, total = gastos.sumOf { g -> g.valor })
                }
            }
        }
    }

    private fun observarOrcamentos() {
        viewModelScope.launch {
            obterOrcamentosComProgressoUseCase(uiState.value.mesReferencia).collect { list ->
                _uiState.update { it.copy(orcamentos = list) }
            }
        }
    }

    private fun String.filterMoney(): String {
        val filtered = filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        val firstDot = filtered.indexOf('.')
        return if (firstDot == -1) filtered else {
            filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
        }
    }
}
