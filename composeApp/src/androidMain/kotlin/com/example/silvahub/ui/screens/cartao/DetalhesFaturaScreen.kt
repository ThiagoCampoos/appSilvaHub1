package com.example.silvahub.ui.screens.cartao

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesFaturaScreen(
    faturaId: Long,
    onBack: () -> Unit,
    onOpenCompra: (Long) -> Unit,
    viewModel: DetalhesFaturaViewModel = koinViewModel(parameters = { parametersOf(faturaId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.limparMensagens()
        }
        uiState.errorMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fatura ${uiState.detalhe?.fatura?.mesReferencia ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        val detalhe = uiState.detalhe
        if (detalhe == null) {
            Text("Carregando…", modifier = Modifier.padding(padding).padding(16.dp))
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            MoneyFormat.formatCentavos(detalhe.valorTotalCentavos),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Pago: ${MoneyFormat.formatCentavos(detalhe.fatura.valorPagoCentavos)}",
                        )
                        Text(
                            "Pendente: ${MoneyFormat.formatCentavos(detalhe.saldoPendenteCentavos)}",
                        )
                        Text(
                            "Status: ${if (detalhe.fatura.status == EStatusFatura.PAGA) "Paga" else "Aberta"}",
                        )
                        Text(
                            "Vencimento: ${dateFormat.format(Date(detalhe.fatura.dataVencimento))}",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (detalhe.saldoPendenteCentavos > 0) {
                            Button(onClick = viewModel::openPagamento, modifier = Modifier.fillMaxWidth()) {
                                Text("Pagar fatura")
                            }
                        }
                    }
                }
            }

            item { Text("Lançamentos", fontWeight = FontWeight.Bold) }
            items(detalhe.parcelas, key = { it.parcela.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCompra(item.compra.id) },
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.compra.descricao, fontWeight = FontWeight.SemiBold)
                        Text(MoneyFormat.formatCentavos(item.parcela.valorCentavos))
                        if (item.compra.totalParcelas != null) {
                            Text("Parcela ${item.parcela.numeroParcela}/${item.compra.totalParcelas}")
                        }
                    }
                }
            }

            item { Text("Pagamentos", fontWeight = FontWeight.Bold) }
            items(detalhe.pagamentos, key = { it.id }) { pag ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(MoneyFormat.formatCentavos(pag.valorCentavos))
                            Text(dateFormat.format(Date(pag.data)))
                            if (pag.estornado) Text("Estornado", color = MaterialTheme.colorScheme.error)
                        }
                        if (!pag.estornado) {
                            TextButton(onClick = { viewModel.estornarPagamento(pag.id) }) {
                                Text("Estornar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showPagamentoDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closePagamento,
            title = { Text("Pagar fatura") },
            text = {
                OutlinedTextField(
                    value = uiState.valorPagamentoInput,
                    onValueChange = viewModel::onValorPagamentoChange,
                    label = { Text("Valor (R$)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::pagar) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closePagamento) { Text("Cancelar") }
            },
        )
    }
}
