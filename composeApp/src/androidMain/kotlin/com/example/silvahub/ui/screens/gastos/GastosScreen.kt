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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.model.label
import com.example.silvahub.ui.components.LancamentoBadge
import com.example.silvahub.ui.components.LancamentoDetalhesDialog
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
    onOpenDetalheGasto: (Long) -> Unit,
    onOpenDetalheCompra: (Long) -> Unit,
    onOpenCartao: () -> Unit = {},
    viewModel: GastosViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var dialogLancamento by remember { mutableStateOf<Lancamento?>(null) }

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
            Text(
                "Total do mês: ${MoneyFormat.format(uiState.total)}",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.lancamentos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Nenhum lançamento este mês")
                    Text(
                        "Toque em + para lançar rapidamente",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                val grouped = uiState.lancamentos.groupBy { dayKey(it.data) }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    grouped.forEach { (day, items) ->
                        item {
                            Text(
                                day,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        items(items, key = { it.id }) { lancamento ->
                            LancamentoItem(
                                lancamento = lancamento,
                                onClick = { dialogLancamento = lancamento },
                                onDelete = {
                                    if (lancamento.gastoId != null) {
                                        viewModel.deletarGasto(lancamento)
                                    }
                                },
                                canSwipeDelete = lancamento.gastoId != null,
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    dialogLancamento?.let { lancamento ->
        LancamentoDetalhesDialog(
            lancamento = lancamento,
            onDismiss = { dialogLancamento = null },
            onOpenDetalhe = {
                dialogLancamento = null
                when {
                    lancamento.gastoId != null -> onOpenDetalheGasto(lancamento.gastoId)
                    lancamento.compraCartaoId != null -> onOpenDetalheCompra(lancamento.compraCartaoId)
                }
            },
        )
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
                Text(
                    "Novo lançamento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
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
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModoLancamento.entries.forEach { modo ->
                        FilterChip(
                            selected = uiState.modo == modo,
                            onClick = { viewModel.onModoChange(modo) },
                            label = {
                                Text(
                                    when (modo) {
                                        ModoLancamento.DEBITO_AVISTA -> "Débito à vista"
                                        ModoLancamento.CREDITO_AVISTA -> "Crédito à vista"
                                        ModoLancamento.CREDITO_PARCELADO -> "Crédito parcelado"
                                        ModoLancamento.CREDITO_RECORRENTE -> "Crédito recorrente"
                                    },
                                )
                            },
                        )
                    }
                }
                if (uiState.modo == ModoLancamento.CREDITO_PARCELADO) {
                    OutlinedTextField(
                        value = uiState.parcelasInput,
                        onValueChange = viewModel::onParcelasChange,
                        label = { Text("Nº de parcelas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (uiState.needsCartaoConfig ||
                    (uiState.modo != ModoLancamento.DEBITO_AVISTA && !uiState.cartaoConfigurado)
                ) {
                    Text(
                        "Cartão não configurado",
                        color = MaterialTheme.colorScheme.error,
                    )
                    TextButton(onClick = onOpenCartao) {
                        Text("Configurar cartão")
                    }
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
private fun LancamentoItem(
    lancamento: Lancamento,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    canSwipeDelete: Boolean,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart && canSwipeDelete) {
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
        enableDismissFromEndToStart = canSwipeDelete,
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
                    Text(lancamento.descricao, fontWeight = FontWeight.SemiBold)
                    Text(
                        lancamento.categoria.label(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    LancamentoBadge(lancamento = lancamento)
                }
                Text(
                    MoneyFormat.format(lancamento.valor),
                    color = SecondaryRed,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun dayKey(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(timestamp))
}
