package com.example.silvahub.ui.screens.historico

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.label
import com.example.silvahub.ui.theme.SecondaryRed
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoricoScreen(
    onOpenDetalhe: (Long) -> Unit,
    viewModel: HistoricoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gastosFiltrados = uiState.gastos.filter {
        uiState.filtroCategoria == null || it.categoria == uiState.filtroCategoria
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Histórico", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = viewModel::mesAnterior) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
            }
            Text(uiState.mesReferencia, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = viewModel::mesProximo) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
            }
        }

        Text("Total: ${MoneyFormat.format(gastosFiltrados.sumOf { it.valor })}")

        if (uiState.insights.isNotEmpty()) {
            Text("Insights", style = MaterialTheme.typography.titleMedium)
            uiState.insights.take(4).forEach { insight ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(insight.titulo, fontWeight = FontWeight.SemiBold)
                        Text(insight.descricao, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Text("Filtro")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = uiState.filtroCategoria == null,
                onClick = { viewModel.filtrarCategoria(null) },
                label = { Text("Todas") },
            )
            ECategoriaGasto.entries.forEach { cat ->
                FilterChip(
                    selected = uiState.filtroCategoria == cat,
                    onClick = { viewModel.filtrarCategoria(cat) },
                    label = { Text(cat.label()) },
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(gastosFiltrados, key = { it.id }) { gasto ->
                Card(
                    onClick = { onOpenDetalhe(gasto.id) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
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
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
