package com.example.silvahub.ui.screens.gastos

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Gasto
import com.example.silvahub.domain.model.label
import com.example.silvahub.ui.theme.SecondaryRed
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GastosScreen(
    openSheetOnStart: Boolean = false,
    onOpenDetalhe: (Long) -> Unit,
    viewModel: GastosViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(openSheetOnStart) {
        if (openSheetOnStart) viewModel.openSheet(fromDeepLink = true)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagens()
        }
        uiState.successMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (msg.contains("removido", ignoreCase = true)) "Desfazer" else null,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.desfazerDelete()
            }
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openSheet() }) {
                Icon(Icons.Default.Add, contentDescription = "Novo gasto")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Text("Gastos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Total do mês: ${MoneyFormat.format(uiState.total)}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.gastos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Nenhum gasto este mês")
                    Text("Toque em + para lançar rapidamente", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val grouped = uiState.gastos.groupBy { dayKey(it.data) }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    grouped.forEach { (day, items) ->
                        item {
                            Text(day, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        }
                        items(items, key = { it.id }) { gasto ->
                            GastoItem(
                                gasto = gasto,
                                onClick = { onOpenDetalhe(gasto.id) },
                                onDelete = { viewModel.deletarGasto(gasto) },
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (uiState.showSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeSheet,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Novo gasto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = uiState.valorInput,
                    onValueChange = viewModel::onValorChange,
                    label = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = uiState.descricaoInput,
                    onValueChange = viewModel::onDescricaoChange,
                    label = { Text("Descrição") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Categoria")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ECategoriaGasto.entries.forEach { cat ->
                        FilterChip(
                            selected = uiState.categoria == cat,
                            onClick = { viewModel.onCategoriaChange(cat) },
                            label = { Text(cat.label()) },
                        )
                    }
                }
                Text("Tipo")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModoLancamento.entries.forEach { modo ->
                        FilterChip(
                            selected = uiState.modo == modo,
                            onClick = { viewModel.onModoChange(modo) },
                            label = {
                                Text(
                                    when (modo) {
                                        ModoLancamento.AVISTA -> "À vista"
                                        ModoLancamento.PARCELADO -> "Parcelado"
                                        ModoLancamento.RECORRENTE -> "Recorrente"
                                    },
                                )
                            },
                        )
                    }
                }
                if (uiState.modo == ModoLancamento.PARCELADO) {
                    OutlinedTextField(
                        value = uiState.parcelasInput,
                        onValueChange = viewModel::onParcelasChange,
                        label = { Text("Nº de parcelas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Button(
                    onClick = viewModel::salvarGasto,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Salvar")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GastoItem(gasto: Gasto, onClick: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        enableDismissFromStartToEnd = false,
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(gasto.descricao, fontWeight = FontWeight.SemiBold)
                    Text(gasto.categoria.label(), style = MaterialTheme.typography.bodySmall)
                    when {
                        gasto.parcelaAtual != null && gasto.totalParcelas != null -> {
                            AssistChip(onClick = {}, label = { Text("${gasto.parcelaAtual}/${gasto.totalParcelas}") })
                        }
                        gasto.tipo == ETipoGasto.RECORRENTE -> {
                            AssistChip(onClick = {}, label = { Text("Recorrente") })
                        }
                    }
                }
                Text(MoneyFormat.format(gasto.valor), color = SecondaryRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun dayKey(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(timestamp))
}
