package com.example.silvahub.ui.screens.configuracoes

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.data.preferences.ThemeMode
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.label
import com.example.silvahub.domain.usecase.ExportarGastosCsvUseCase
import com.example.silvahub.ui.screens.home.OrcamentoBar
import com.example.silvahub.util.MoneyFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfiguracoesScreen(
    onEditConta: (Long) -> Unit,
    onOpenCartao: () -> Unit = {},
    viewModel: ConfiguracoesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportarCsv: ExportarGastosCsvUseCase = koinInject()
    var showImportConfirm by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        val json = uiState.pendingExportJson ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                }
                viewModel.clearPendingExport()
            }
        } else {
            viewModel.clearPendingExport()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportConfirm = true
        }
    }

    LaunchedEffect(uiState.pendingExportJson) {
        uiState.pendingExportJson?.let {
            exportLauncher.launch("silvahub-backup-${System.currentTimeMillis()}.json")
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagens()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagens()
        }
    }

    if (showImportConfirm) {
        AlertDialog(
            onDismissRequest = { showImportConfirm = false },
            title = { Text("Restaurar backup?") },
            text = { Text("Isso substitui todos os dados atuais. Continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportConfirm = false
                        val uri = pendingImportUri ?: return@TextButton
                        scope.launch {
                            val json = withContext(Dispatchers.IO) {
                                context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                            }
                            if (json != null) viewModel.importarBackup(json)
                        }
                    },
                ) { Text("Restaurar") }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = false }) { Text("Cancelar") }
            },
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Salário mensal", style = MaterialTheme.typography.titleMedium)
                        Text("Atual: ${uiState.salarioAtual?.let(MoneyFormat::format) ?: "Não definido"}")
                        OutlinedTextField(
                            value = uiState.salarioInput,
                            onValueChange = viewModel::onSalarioInputChange,
                            label = { Text("Valor") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(onClick = viewModel::salvarSalario, enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth()) {
                            Text("Salvar salário")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCartao() },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cartão de crédito", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Limite, fechamento, vencimento e faturas",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rendas extras", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = uiState.extraDescricaoInput,
                            onValueChange = viewModel::onExtraDescricaoChange,
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = uiState.extraValorInput,
                            onValueChange = viewModel::onExtraValorChange,
                            label = { Text("Valor") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(onClick = viewModel::adicionarExtra, modifier = Modifier.fillMaxWidth()) {
                            Text("Adicionar renda extra")
                        }
                        uiState.salariosExtras.forEach { extra ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("${extra.descricao}: ${MoneyFormat.format(extra.valor)}")
                                TextButton(onClick = { viewModel.deletarExtra(extra.id) }) { Text("Remover") }
                            }
                        }
                    }
                }
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Nova conta fixa", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = uiState.contaNomeInput, onValueChange = viewModel::onContaNomeInputChange, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = uiState.contaValorInput, onValueChange = viewModel::onContaValorInputChange, label = { Text("Valor") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = uiState.contaDiaVencimentoInput, onValueChange = viewModel::onContaDiaInputChange, label = { Text("Dia (1-31)") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = viewModel::adicionarContaFixa, modifier = Modifier.fillMaxWidth()) { Text("Adicionar conta fixa") }
                    }
                }
            }

            item { Text("Contas fixas", style = MaterialTheme.typography.titleMedium) }
            items(uiState.contasFixas, key = { it.id }) { conta ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onEditConta(conta.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(conta.nome, fontWeight = FontWeight.SemiBold)
                            Text("${MoneyFormat.format(conta.valor)} · dia ${conta.diaVencimento}")
                        }
                        OutlinedButton(onClick = { viewModel.deletarContaFixa(conta.id) }) { Text("Remover") }
                    }
                }
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Orçamentos por categoria", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ECategoriaGasto.entries.forEach { cat ->
                                FilterChip(
                                    selected = uiState.orcamentoCategoria == cat,
                                    onClick = { viewModel.onOrcamentoCategoriaChange(cat) },
                                    label = { Text(cat.label()) },
                                )
                            }
                        }
                        OutlinedTextField(
                            value = uiState.orcamentoLimiteInput,
                            onValueChange = viewModel::onOrcamentoLimiteChange,
                            label = { Text("Limite mensal") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(onClick = viewModel::salvarOrcamento, modifier = Modifier.fillMaxWidth()) {
                            Text("Definir orçamento")
                        }
                        uiState.orcamentos.forEach { item ->
                            OrcamentoBar(
                                label = item.orcamento.categoria.label(),
                                gasto = item.gastoAtual,
                                limite = item.orcamento.limiteMensal,
                                percentual = item.percentual,
                            )
                            TextButton(onClick = { viewModel.deletarOrcamento(item.orcamento.id) }) {
                                Text("Remover orçamento")
                            }
                        }
                    }
                }
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Tema", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = uiState.themeMode == mode,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    label = {
                                        Text(
                                            when (mode) {
                                                ThemeMode.SYSTEM -> "Sistema"
                                                ThemeMode.LIGHT -> "Claro"
                                                ThemeMode.DARK -> "Escuro"
                                            },
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Backup e exportação", style = MaterialTheme.typography.titleMedium)
                        uiState.lastBackupAt?.let {
                            Text(
                                "Último backup: ${
                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date(it))
                                }",
                            )
                        }
                        Button(onClick = viewModel::exportarBackup, modifier = Modifier.fillMaxWidth()) {
                            Text("Exportar backup JSON")
                        }
                        OutlinedButton(
                            onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Importar backup")
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    runCatching {
                                        val csv = exportarCsv()
                                        val file = File(context.cacheDir, "gastos.csv")
                                        withContext(Dispatchers.IO) { file.writeText(csv) }
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file,
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/csv"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Exportar CSV"))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Exportar gastos CSV")
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
