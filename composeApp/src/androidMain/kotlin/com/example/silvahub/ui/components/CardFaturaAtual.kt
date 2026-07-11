package com.example.silvahub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.FaturaDetalhe
import com.example.silvahub.util.MoneyFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CardFaturaAtual(
    detalhe: FaturaDetalhe,
    onOpenDetalhes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetalhes),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Fatura do cartão",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                MoneyFormat.formatCentavos(detalhe.valorTotalCentavos),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Vencimento: ${dateFormat.format(Date(detalhe.fatura.dataVencimento))}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    if (detalhe.fatura.status == EStatusFatura.PAGA) "Paga" else "Aberta",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (detalhe.fatura.status == EStatusFatura.PAGA) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
            if (detalhe.saldoPendenteCentavos > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pendente: ${MoneyFormat.formatCentavos(detalhe.saldoPendenteCentavos)}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TextButton(onClick = onOpenDetalhes) {
                Text("Ver fatura")
            }
        }
    }
}
