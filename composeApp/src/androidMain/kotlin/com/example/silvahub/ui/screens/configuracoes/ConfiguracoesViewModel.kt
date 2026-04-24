package com.example.silvahub.ui.screens.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfiguracoesUiState(
    val salarioInput: String = "",
    val salarioAtual: Double? = null,
    val contaNomeInput: String = "",
    val contaValorInput: String = "",
    val contaDiaVencimentoInput: String = "",
    val contasFixas: List<ContaFixa> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

class ConfiguracoesViewModel(
    private val salvarSalarioUseCase: SalvarSalarioUseCase,
    private val obterUltimoSalarioUseCase: ObterUltimoSalarioUseCase,
    private val adicionarContaFixaUseCase: AdicionarContaFixaUseCase,
    private val obterContasFixasUseCase: ObterContasFixasUseCase,
    private val deletarContaFixaUseCase: DeletarContaFixaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracoesUiState())
    val uiState: StateFlow<ConfiguracoesUiState> = _uiState.asStateFlow()

    init {
        observarUltimoSalario()
        observarContasFixas()
    }

    fun onSalarioInputChange(valor: String) {
        _uiState.update { it.copy(salarioInput = valor.filterDigitsAndDecimal()) }
    }

    fun onContaNomeInputChange(nome: String) {
        _uiState.update { it.copy(contaNomeInput = nome) }
    }

    fun onContaValorInputChange(valor: String) {
        _uiState.update { it.copy(contaValorInput = valor.filterDigitsAndDecimal()) }
    }

    fun onContaDiaInputChange(dia: String) {
        _uiState.update { it.copy(contaDiaVencimentoInput = dia.filter { ch -> ch.isDigit() }.take(2)) }
    }

    fun limparMensagens() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun salvarSalario() {
        val valor = uiState.value.salarioInput.toDoubleOrNull()
        if (valor == null || valor <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Informe um salario valido (maior que zero)") }
            return
        }

        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                salvarSalarioUseCase(
                    Salario(
                        valor = valor,
                        mesReferencia = mesReferenciaAtual(),
                    ),
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        salarioInput = "",
                        successMessage = "Salario salvo com sucesso",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao salvar salario",
                    )
                }
            }
        }
    }

    fun adicionarContaFixa() {
        val state = uiState.value
        val valor = state.contaValorInput.toDoubleOrNull()
        val dia = state.contaDiaVencimentoInput.toIntOrNull()

        when {
            state.contaNomeInput.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Informe o nome da conta fixa") }
                return
            }

            valor == null || valor <= 0.0 -> {
                _uiState.update { it.copy(errorMessage = "Informe um valor valido para a conta") }
                return
            }

            dia == null || dia !in 1..31 -> {
                _uiState.update { it.copy(errorMessage = "Dia de vencimento deve estar entre 1 e 31") }
                return
            }
        }

        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                adicionarContaFixaUseCase(
                    ContaFixa(
                        nome = state.contaNomeInput.trim(),
                        valor = valor,
                        diaVencimento = dia,
                    ),
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        contaNomeInput = "",
                        contaValorInput = "",
                        contaDiaVencimentoInput = "",
                        successMessage = "Conta fixa adicionada com sucesso",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao adicionar conta fixa",
                    )
                }
            }
        }
    }

    fun deletarContaFixa(id: Long) {
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                deletarContaFixaUseCase(id)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Conta fixa removida com sucesso",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao remover conta fixa",
                    )
                }
            }
        }
    }

    private fun observarUltimoSalario() {
        viewModelScope.launch {
            obterUltimoSalarioUseCase().collect { salario ->
                _uiState.update { it.copy(salarioAtual = salario?.valor) }
            }
        }
    }

    private fun observarContasFixas() {
        viewModelScope.launch {
            obterContasFixasUseCase().collect { contas ->
                _uiState.update { it.copy(contasFixas = contas) }
            }
        }
    }

    private fun mesReferenciaAtual(): String {
        return SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    }

    private fun String.filterDigitsAndDecimal(): String {
        val filtered = filter { it.isDigit() || it == '.' }
        val firstDot = filtered.indexOf('.')
        return if (firstDot == -1) {
            filtered
        } else {
            val before = filtered.substring(0, firstDot + 1)
            val after = filtered.substring(firstDot + 1).replace(".", "")
            before + after
        }
    }
}

