package com.example.silvahub.ui.screens.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silvahub.data.preferences.ThemeMode
import com.example.silvahub.data.preferences.UserPreferencesRepository
import com.example.silvahub.domain.model.ContaFixa
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Orcamento
import com.example.silvahub.domain.model.OrcamentoComProgresso
import com.example.silvahub.domain.model.Salario
import com.example.silvahub.domain.model.SalarioExtra
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.AdicionarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.DefinirOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarOrcamentoUseCase
import com.example.silvahub.domain.usecase.DeletarSalarioExtraUseCase
import com.example.silvahub.domain.usecase.ExportarBackupUseCase
import com.example.silvahub.domain.usecase.ImportarBackupUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterOrcamentosComProgressoUseCase
import com.example.silvahub.domain.usecase.ObterSalariosExtrasUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
import com.example.silvahub.util.DateUtils
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
    val extraDescricaoInput: String = "",
    val extraValorInput: String = "",
    val salariosExtras: List<SalarioExtra> = emptyList(),
    val orcamentoCategoria: ECategoriaGasto = ECategoriaGasto.ALIMENTACAO,
    val orcamentoLimiteInput: String = "",
    val orcamentos: List<OrcamentoComProgresso> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val lastBackupAt: Long? = null,
    val pendingExportJson: String? = null,
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
    private val adicionarSalarioExtraUseCase: AdicionarSalarioExtraUseCase,
    private val obterSalariosExtrasUseCase: ObterSalariosExtrasUseCase,
    private val deletarSalarioExtraUseCase: DeletarSalarioExtraUseCase,
    private val definirOrcamentoUseCase: DefinirOrcamentoUseCase,
    private val deletarOrcamentoUseCase: DeletarOrcamentoUseCase,
    private val obterOrcamentosComProgressoUseCase: ObterOrcamentosComProgressoUseCase,
    private val exportarBackupUseCase: ExportarBackupUseCase,
    private val importarBackupUseCase: ImportarBackupUseCase,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracoesUiState())
    val uiState: StateFlow<ConfiguracoesUiState> = _uiState.asStateFlow()

    private val mes = DateUtils.mesReferenciaAtual()

    init {
        observarUltimoSalario()
        observarContasFixas()
        observarExtras()
        observarOrcamentos()
        observarPreferencias()
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

    fun onExtraDescricaoChange(value: String) = _uiState.update { it.copy(extraDescricaoInput = value) }
    fun onExtraValorChange(value: String) = _uiState.update { it.copy(extraValorInput = value.filterDigitsAndDecimal()) }
    fun onOrcamentoCategoriaChange(value: ECategoriaGasto) = _uiState.update { it.copy(orcamentoCategoria = value) }
    fun onOrcamentoLimiteChange(value: String) = _uiState.update { it.copy(orcamentoLimiteInput = value.filterDigitsAndDecimal()) }

    fun limparMensagens() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun clearPendingExport() = _uiState.update { it.copy(pendingExportJson = null) }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun salvarSalario() {
        val valor = uiState.value.salarioInput.toDoubleOrNull()
        if (valor == null || valor <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Informe um salário válido (maior que zero)") }
            return
        }
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                salvarSalarioUseCase(Salario(valor = valor, mesReferencia = mes))
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, salarioInput = "", successMessage = "Salário salvo com sucesso") }
            }.onFailure { t ->
                _uiState.update { it.copy(isLoading = false, errorMessage = t.message ?: "Erro ao salvar salário") }
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
                _uiState.update { it.copy(errorMessage = "Informe um valor válido para a conta") }
                return
            }
            dia == null || dia !in 1..31 -> {
                _uiState.update { it.copy(errorMessage = "Dia de vencimento deve estar entre 1 e 31") }
                return
            }
        }
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }
                adicionarContaFixaUseCase(
                    ContaFixa(nome = state.contaNomeInput.trim(), valor = valor!!, diaVencimento = dia!!),
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        contaNomeInput = "",
                        contaValorInput = "",
                        contaDiaVencimentoInput = "",
                        successMessage = "Conta fixa adicionada",
                    )
                }
            }.onFailure { t ->
                _uiState.update { it.copy(isLoading = false, errorMessage = t.message ?: "Erro ao adicionar conta") }
            }
        }
    }

    fun deletarContaFixa(id: Long) {
        viewModelScope.launch {
            runCatching { deletarContaFixaUseCase(id) }
                .onSuccess { _uiState.update { it.copy(successMessage = "Conta removida") } }
                .onFailure { t -> _uiState.update { it.copy(errorMessage = t.message) } }
        }
    }

    fun adicionarExtra() {
        val state = uiState.value
        val valor = state.extraValorInput.toDoubleOrNull()
        if (state.extraDescricaoInput.isBlank() || valor == null || valor <= 0) {
            _uiState.update { it.copy(errorMessage = "Preencha descrição e valor da renda extra") }
            return
        }
        viewModelScope.launch {
            runCatching {
                adicionarSalarioExtraUseCase(
                    SalarioExtra(
                        descricao = state.extraDescricaoInput.trim(),
                        valor = valor,
                        mesReferencia = mes,
                    ),
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(extraDescricaoInput = "", extraValorInput = "", successMessage = "Renda extra adicionada")
                }
            }.onFailure { t -> _uiState.update { it.copy(errorMessage = t.message) } }
        }
    }

    fun deletarExtra(id: Long) {
        viewModelScope.launch {
            runCatching { deletarSalarioExtraUseCase(id) }
                .onSuccess { _uiState.update { it.copy(successMessage = "Renda extra removida") } }
        }
    }

    fun salvarOrcamento() {
        val limite = uiState.value.orcamentoLimiteInput.toDoubleOrNull()
        if (limite == null || limite <= 0) {
            _uiState.update { it.copy(errorMessage = "Informe um limite válido") }
            return
        }
        viewModelScope.launch {
            runCatching {
                definirOrcamentoUseCase(
                    Orcamento(categoria = uiState.value.orcamentoCategoria, limiteMensal = limite),
                )
            }.onSuccess {
                _uiState.update { it.copy(orcamentoLimiteInput = "", successMessage = "Orçamento definido") }
            }.onFailure { t -> _uiState.update { it.copy(errorMessage = t.message) } }
        }
    }

    fun deletarOrcamento(id: Long) {
        viewModelScope.launch { deletarOrcamentoUseCase(id) }
    }

    fun exportarBackup() {
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }
                exportarBackupUseCase()
            }.onSuccess { json ->
                preferencesRepository.setLastBackupAt(System.currentTimeMillis())
                _uiState.update {
                    it.copy(isLoading = false, pendingExportJson = json, successMessage = "Backup pronto para salvar")
                }
            }.onFailure { t ->
                _uiState.update { it.copy(isLoading = false, errorMessage = t.message ?: "Erro no backup") }
            }
        }
    }

    fun importarBackup(json: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }
                importarBackupUseCase(json)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Backup restaurado") }
            }.onFailure { t ->
                _uiState.update { it.copy(isLoading = false, errorMessage = t.message ?: "Backup inválido") }
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

    private fun observarExtras() {
        viewModelScope.launch {
            obterSalariosExtrasUseCase(mes).collect { extras ->
                _uiState.update { it.copy(salariosExtras = extras) }
            }
        }
    }

    private fun observarOrcamentos() {
        viewModelScope.launch {
            obterOrcamentosComProgressoUseCase(mes).collect { list ->
                _uiState.update { it.copy(orcamentos = list) }
            }
        }
    }

    private fun observarPreferencias() {
        viewModelScope.launch {
            preferencesRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.lastBackupAt.collect { ts ->
                _uiState.update { it.copy(lastBackupAt = ts) }
            }
        }
    }

    private fun String.filterDigitsAndDecimal(): String {
        val filtered = filter { it.isDigit() || it == '.' }
        val firstDot = filtered.indexOf('.')
        return if (firstDot == -1) filtered else {
            filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
        }
    }
}
