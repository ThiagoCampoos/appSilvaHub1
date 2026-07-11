package com.example.silvahub.ui.screens.cartao

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.label
import com.example.silvahub.util.MoneyFormat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesCompraCartaoScreen(
    compraId: Long,
    onBack: () -> Unit,
    viewModel: DetalhesCompraCartaoViewModel = koinViewModel(parameters = { parametersOf(compraId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onBack()
    }
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
                title = { Text("Compra no cartão") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                OutlinedTextField(
                    value = uiState.descricaoInput,
                    onValueChange = viewModel::onDescricaoChange,
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.valorInput,
                    onValueChange = viewModel::onValorChange,
                    label = { Text("Valor total (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoria", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ECategoriaGasto.entries.take(4).forEach { cat ->
                        FilterChip(
                            selected = uiState.categoria == cat,
                            onClick = { viewModel.onCategoriaChange(cat) },
                            label = { Text(cat.label()) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = viewModel::salvarEdicao, modifier = Modifier.fillMaxWidth()) {
                    Text("Salvar alterações")
                }
            }

            item { Text("Parcelas", fontWeight = FontWeight.Bold) }
            items(uiState.parcelas, key = { it.id }) { parcela ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = parcela.numeroParcela in uiState.parcelasSelecionadas,
                        onCheckedChange = { viewModel.toggleParcela(parcela.numeroParcela) },
                    )
                    Text(
                        "Parcela ${parcela.numeroParcela}: ${MoneyFormat.formatCentavos(parcela.valorCentavos)}",
                    )
                }
            }

            item {
                Button(onClick = viewModel::anteciparSelecionadas, modifier = Modifier.fillMaxWidth()) {
                    Text("Antecipar selecionadas")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = viewModel::estornar, modifier = Modifier.fillMaxWidth()) {
                    Text("Estornar compra")
                }
                TextButton(onClick = viewModel::excluir, modifier = Modifier.fillMaxWidth()) {
                    Text("Excluir compra")
                }
                if (uiState.compra?.tipo == ETipoCompraCartao.CREDITO_RECORRENTE &&
                    uiState.compra?.recorrenciaId != null
                ) {
                    TextButton(onClick = viewModel::cancelarRecorrencia, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancelar recorrência")
                    }
                }
            }
        }
    }
}
