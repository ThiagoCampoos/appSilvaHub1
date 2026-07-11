package com.example.silvahub.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.label
import com.example.silvahub.ui.components.SilvaHubLogo
import com.example.silvahub.ui.theme.PrimaryGreen
import com.example.silvahub.ui.theme.SecondaryRed
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenGastos: () -> Unit,
    onOpenGraficos: () -> Unit,
    onOpenConfiguracoes: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resumo = uiState.resumo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SilvaHubLogo(size = 36.dp)
                        Text(
                            text = "SilvaHub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenGraficos) {
                        Icon(Icons.Outlined.BarChart, contentDescription = "Gráficos")
                    }
                },
            )
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Saldo disponível", style = MaterialTheme.typography.titleMedium)
                                val saldo = resumo?.saldoDisponivel ?: 0.0
                                Text(
                                    text = MoneyFormat.format(saldo),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (saldo >= 0) PrimaryGreen else SecondaryRed,
                                )
                                Text(
                                    text = "Pode gastar ${MoneyFormat.format(resumo?.gastoDiarioSugerido ?: 0.0)}/dia",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                        }
                    }

                    if (resumo != null && resumo.salario <= 0.0) {
                        item {
                            Card {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Defina seu salário para ver o saldo real")
                                    TextButton(onClick = onOpenConfiguracoes) {
                                        Text("Ir para Configurações")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            MiniCard(
                                "Salário",
                                MoneyFormat.format((resumo?.salario ?: 0.0) + (resumo?.salariosExtras ?: 0.0)),
                                Modifier.weight(1f),
                            )
                            MiniCard("Fixas", MoneyFormat.format(resumo?.contasFixas ?: 0.0), Modifier.weight(1f))
                            MiniCard("Gastos", MoneyFormat.format(resumo?.gastos ?: 0.0), Modifier.weight(1f))
                        }
                    }

                    if (resumo != null && resumo.salario + resumo.salariosExtras > 0) {
                        item {
                            val comprometido = (resumo.contasFixas + resumo.gastos) /
                                (resumo.salario + resumo.salariosExtras)
                            Text("Comprometimento da renda", style = MaterialTheme.typography.titleSmall)
                            LinearProgressIndicator(
                                progress = { comprometido.toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                            )
                            Text("${(comprometido * 100).toInt()}%")
                        }
                    }

                    if (uiState.orcamentos.isNotEmpty()) {
                        item {
                            Text(
                                "Orçamentos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        items(uiState.orcamentos) { item ->
                            OrcamentoBar(
                                item.orcamento.categoria.label(),
                                item.gastoAtual,
                                item.orcamento.limiteMensal,
                                item.percentual,
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Últimos gastos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            TextButton(onClick = onOpenGastos) { Text("Ver todos") }
                        }
                    }

                    if (uiState.ultimosGastos.isEmpty()) {
                        item { Text("Nenhum gasto ainda. Lance o primeiro!") }
                    } else {
                        items(uiState.ultimosGastos, key = { it.id }) { gasto ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(gasto.descricao, fontWeight = FontWeight.SemiBold)
                                        Text(gasto.categoria.label(), style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(MoneyFormat.format(gasto.valor), color = SecondaryRed)
                                }
                            }
                        }
                    }
                    item { Box(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MiniCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun OrcamentoBar(label: String, gasto: Double, limite: Double, percentual: Float) {
    val color = when {
        percentual >= 1f -> SecondaryRed
        percentual >= 0.8f -> Color(0xFFF9A825)
        else -> PrimaryGreen
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontWeight = FontWeight.SemiBold)
                Text("${MoneyFormat.format(gasto)} / ${MoneyFormat.format(limite)}")
            }
            LinearProgressIndicator(
                progress = { percentual.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                color = color,
            )
        }
    }
}
