package com.example.silvahub.ui.screens.gastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.label
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesGastoScreen(
    gastoId: Long,
    onBack: () -> Unit,
    viewModel: DetalhesGastoViewModel = koinViewModel(parameters = { parametersOf(gastoId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do gasto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        val gasto = uiState.gasto
        if (gasto == null) {
            Text("Carregando...", modifier = Modifier.padding(padding).padding(16.dp))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(gasto.descricao, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(MoneyFormat.format(gasto.valor), style = MaterialTheme.typography.displaySmall)
                Text("Categoria: ${gasto.categoria.label()}")
                Text("Tipo: ${gasto.tipo.name}")
                Text(
                    "Data: ${SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(gasto.data))}",
                )
                if (gasto.parcelaAtual != null && gasto.totalParcelas != null) {
                    Text("Parcela ${gasto.parcelaAtual}/${gasto.totalParcelas}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.deletar() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Excluir")
                }
                if (gasto.grupoParcelamentoId != null) {
                    OutlinedButton(
                        onClick = { viewModel.deletar(excluirRestantes = true) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Excluir parcelas restantes")
                    }
                }
            }
        }
    }
}
