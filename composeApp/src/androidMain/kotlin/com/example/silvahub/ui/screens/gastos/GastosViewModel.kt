package com.example.silvahub.ui.screens.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.model.OrcamentoComProgresso
import com.example.silvahub.domain.usecase.AdicionarGastoUseCase
import com.example.silvahub.domain.usecase.CriarRecorrenciaCartaoUseCase
import com.example.silvahub.domain.usecase.DeletarGastoUseCase
import com.example.silvahub.domain.usecase.ObterCartaoUseCase
import com.example.silvahub.domain.usecase.ObterLancamentosDoMesUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.domain.usecase.RegistrarCompraCartaoUseCase
import com.example.silvahub.util.DateUtils
import com.example.silvahub.util.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ModoLancamento {
    DEBITO_AVISTA,
    CREDITO_AVISTA,
    CREDITO_PARCELADO,
    CREDITO_RECORRENTE,
}

data class GastosUiState(
    val mesReferencia: String = DateUtils.mesReferenciaAtual(),
    val lancamentos: List<Lancamento> = emptyList(),
    val total: Double = 0.0,
    val orcamentos: List<OrcamentoComProgresso> = emptyList(),
    val cartaoConfigurado: Boolean = false,
    val showSheet: Boolean = false,
    val descricaoInput: String = "",
    val valorInput: String = "",
    val categoria: ECategoriaGasto = ECategoriaGasto.OUTROS,
    val modo: ModoLancamento = ModoLancamento.DEBITO_AVISTA,
    val parcelasInput: String = "2",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val needsCartaoConfig: Boolean = false,
    val openSheetFromDeepLink: Boolean = false,
)

class GastosViewModel(
    private val obterLancamentosDoMesUseCase: ObterLancamentosDoMesUseCase,
    private val adicionarGastoUseCase: AdicionarGastoUseCase,
    private val registrarCompraCartaoUseCase: RegistrarCompraCartaoUseCase,
    private val criarRecorrenciaCartaoUseCase: CriarRecorrenciaCartaoUseCase,
    private val deletarGastoUseCase: DeletarGastoUseCase,
    private val obterOrcamentosComProgressoUseCase: ObterOrcamentosComProgressoUseCase,
    private val obterCartaoUseCase: ObterCartaoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastosUiState())
    val uiState: StateFlow<GastosUiState> = _uiState.asStateFlow()

    private var lastDeleted: Gasto? = null

    init {
        observarLancamentos()
        observarOrcamentos()
        observarCartao()
    }

    fun openSheet(fromDeepLink: Boolean = false) {
        _uiState.update { it.copy(showSheet = true, openSheetFromDeepLink = fromDeepLink) }
    }

    fun closeSheet() {
        _uiState.update { it.copy(showSheet = false, needsCartaoConfig = false) }
    }

    fun onDescricaoChange(value: String) = _uiState.update { it.copy(descricaoInput = value) }
    fun onValorChange(value: String) = _uiState.update { it.copy(valorInput = value.filterMoney()) }
    fun onCategoriaChange(value: ECategoriaGasto) = _uiState.update { it.copy(categoria = value) }
    fun onModoChange(value: ModoLancamento) = _uiState.update {
        it.copy(modo = value, needsCartaoConfig = false)
    }
    fun onParcelasChange(value: String) = _uiState.update {
        it.copy(parcelasInput = value.filter { ch -> ch.isDigit() }.take(2))
    }

    fun limparMensagens() = _uiState.update {
        it.copy(errorMessage = null, successMessage = null)
    }

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

        val precisaCartao = state.modo != ModoLancamento.DEBITO_AVISTA
        if (precisaCartao && !state.cartaoConfigurado) {
            _uiState.update {
                it.copy(
                    needsCartaoConfig = true,
                    errorMessage = "Configure o cartão de crédito em Configurações antes de lançar no crédito",
                )
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                when (state.modo) {
                    ModoLancamento.DEBITO_AVISTA -> {
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
                    ModoLancamento.CREDITO_AVISTA -> {
                        registrarCompraCartaoUseCase(
                            descricao = state.descricaoInput.trim(),
                            valorCentavos = Money.toCentavos(valor),
                            categoria = state.categoria,
                            data = System.currentTimeMillis(),
                            tipo = ETipoCompraCartao.CREDITO_AVISTA,
                        )
                    }
                    ModoLancamento.CREDITO_PARCELADO -> {
                        val parcelas = state.parcelasInput.toIntOrNull() ?: 2
                        registrarCompraCartaoUseCase(
                            descricao = state.descricaoInput.trim(),
                            valorCentavos = Money.toCentavos(valor),
                            categoria = state.categoria,
                            data = System.currentTimeMillis(),
                            tipo = ETipoCompraCartao.CREDITO_PARCELADO,
                            totalParcelas = parcelas,
                        )
                    }
                    ModoLancamento.CREDITO_RECORRENTE -> {
                        criarRecorrenciaCartaoUseCase(
                            descricao = state.descricaoInput.trim(),
                            valorCentavos = Money.toCentavos(valor),
                            categoria = state.categoria,
                            diaCobranca = java.util.Calendar.getInstance()
                                .get(java.util.Calendar.DAY_OF_MONTH)
                                .coerceIn(1, 28),
                        )
                    }
                }
            }.onSuccess {
                val orcamento = uiState.value.orcamentos.find {
                    it.orcamento.categoria == state.categoria
                }
                val estouro = orcamento != null &&
                    (orcamento.gastoAtual + valor) > orcamento.orcamento.limiteMensal
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSheet = false,
                        descricaoInput = "",
                        valorInput = "",
                        modo = ModoLancamento.DEBITO_AVISTA,
                        successMessage = if (estouro) {
                            "Lançamento salvo. Atenção: orçamento de ${state.categoria.name} estourado!"
                        } else {
                            "Lançamento salvo"
                        },
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao salvar lançamento",
                    )
                }
            }
        }
    }

    fun deletarGasto(lancamento: Lancamento, excluirRestantes: Boolean = false) {
        val gastoId = lancamento.gastoId ?: return
        viewModelScope.launch {
            lastDeleted = Gasto(
                id = gastoId,
                descricao = lancamento.descricao,
                valor = lancamento.valor,
                categoria = lancamento.categoria,
                data = lancamento.data,
                tipo = lancamento.tipoGastoLegado ?: ETipoGasto.RAPIDO,
                parcelaAtual = lancamento.parcelaAtual,
                totalParcelas = lancamento.totalParcelas,
                grupoParcelamentoId = lancamento.grupoParcelamentoId,
            )
            runCatching {
                deletarGastoUseCase(gastoId, excluirRestantes)
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
            runCatching { adicionarGastoUseCase(gasto.copy(id = 0)) }
            lastDeleted = null
        }
    }

    private fun observarLancamentos() {
        viewModelScope.launch {
            obterLancamentosDoMesUseCase(uiState.value.mesReferencia).collect { list ->
                _uiState.update {
                    it.copy(lancamentos = list, total = list.sumOf { l -> l.valor })
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

    private fun observarCartao() {
        viewModelScope.launch {
            obterCartaoUseCase().collect { cartao ->
                _uiState.update { it.copy(cartaoConfigurado = cartao != null) }
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
