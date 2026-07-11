package com.example.silvahub.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silvahub.domain.model.ETipoGasto
import com.example.silvahub.domain.model.ETipoLancamento
import com.example.silvahub.domain.model.Lancamento
import com.example.silvahub.domain.model.label

@Composable
fun LancamentoBadge(
    lancamento: Lancamento,
    modifier: Modifier = Modifier,
) {
    val texto = when {
        lancamento.tipoGastoLegado == ETipoGasto.FIXO &&
            lancamento.parcelaAtual != null &&
            lancamento.totalParcelas != null ->
            "Débito parcelado ${lancamento.parcelaAtual}/${lancamento.totalParcelas}"
        lancamento.tipoGastoLegado == ETipoGasto.RECORRENTE -> "Débito recorrente"
        lancamento.tipoLancamento == ETipoLancamento.CREDITO_PARCELADO &&
            lancamento.parcelaAtual != null &&
            lancamento.totalParcelas != null ->
            "${lancamento.tipoLancamento.label()} ${lancamento.parcelaAtual}/${lancamento.totalParcelas}"
        else -> lancamento.tipoLancamento.label()
    }
    AssistChip(
        onClick = {},
        label = { Text(texto) },
        modifier = modifier,
        enabled = false,
    )
}
