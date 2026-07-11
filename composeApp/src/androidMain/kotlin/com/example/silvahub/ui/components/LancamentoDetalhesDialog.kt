package com.example.silvahub.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.model.label
import com.example.silvahub.util.MoneyFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LancamentoDetalhesDialog(
    lancamento: Lancamento,
    onDismiss: () -> Unit,
    onOpenDetalhe: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(lancamento.descricao, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Valor: ${MoneyFormat.format(lancamento.valor)}")
                Spacer(Modifier.height(4.dp))
                Text("Categoria: ${lancamento.categoria.label()}")
                Spacer(Modifier.height(4.dp))
                Text("Data: ${dateFormat.format(Date(lancamento.data))}")
                Spacer(Modifier.height(4.dp))
                Text("Tipo: ${badgeLabel(lancamento)}")
                if (lancamento.parcelaAtual != null && lancamento.totalParcelas != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Parcela: ${lancamento.parcelaAtual}/${lancamento.totalParcelas}")
                }
                if (lancamento.compraCartaoId != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Compra original: #${lancamento.compraCartaoId}")
                }
                if (lancamento.grupoParcelamentoId != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Grupo: ${lancamento.grupoParcelamentoId.take(8)}…")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenDetalhe) { Text("Ver detalhes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        },
    )
}

private fun badgeLabel(lancamento: Lancamento): String = when {
    lancamento.tipoGastoLegado == ETipoGasto.FIXO -> "Débito parcelado (legado)"
    lancamento.tipoGastoLegado == ETipoGasto.RECORRENTE -> "Débito recorrente (legado)"
    else -> lancamento.tipoLancamento.label()
}
