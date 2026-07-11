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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartaoScreen(
    onOpenFatura: (Long) -> Unit,
    viewModel: CartaoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage, uiState.avisoLimite) {
        uiState.successMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.limparMensagens()
        }
        uiState.errorMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.limparMensagens()
        }
        uiState.avisoLimite?.let {
            snackbar.showSnackbar(it)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartão de crédito") },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "config-cartao") {
                Text("Configuração", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.limiteInput,
                    onValueChange = viewModel::onLimiteChange,
                    label = { Text("Limite (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.diaFechamentoInput,
                        onValueChange = viewModel::onFechamentoChange,
                        label = { Text("Dia fechamento") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = uiState.diaVencimentoInput,
                        onValueChange = viewModel::onVencimentoChange,
                        label = { Text("Dia vencimento") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = viewModel::salvar,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (uiState.cartao == null) "Cadastrar cartão" else "Salvar alterações")
                }
            }

            uiState.resumoLimite?.let { resumo ->
                item(key = "resumo-limite") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Limite", fontWeight = FontWeight.Bold)
                            Text("Total: ${MoneyFormat.formatCentavos(resumo.limiteTotalCentavos)}")
                            Text("Utilizado: ${MoneyFormat.formatCentavos(resumo.limiteUtilizadoCentavos)}")
                            Text("Disponível: ${MoneyFormat.formatCentavos(resumo.limiteDisponivelCentavos)}")
                        }
                    }
                }
            }

            if (uiState.recorrencias.isNotEmpty()) {
                item(key = "header-recorrencias") {
                    Text("Recorrências ativas", fontWeight = FontWeight.Bold)
                }
                items(uiState.recorrencias, key = { "rec-${it.id}" }) { rec ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rec.descricao, fontWeight = FontWeight.SemiBold)
                                Text(MoneyFormat.formatCentavos(rec.valorCentavos))
                            }
                            TextButton(onClick = { viewModel.cancelarRecorrencia(rec.id) }) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }

            item(key = "header-faturas") {
                Text("Faturas", fontWeight = FontWeight.Bold)
            }
            if (uiState.faturas.isEmpty()) {
                item(key = "faturas-vazias") { Text("Nenhuma fatura ainda") }
            } else {
                items(uiState.faturas, key = { "fatura-${it.fatura.id}" }) { detalhe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFatura(detalhe.fatura.id) },
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(detalhe.fatura.mesReferencia, fontWeight = FontWeight.SemiBold)
                            Text(MoneyFormat.formatCentavos(detalhe.valorTotalCentavos))
                            Text(
                                if (detalhe.fatura.status == EStatusFatura.PAGA) "Paga" else "Aberta",
                            )
                        }
                    }
                }
            }
        }
    }
}
