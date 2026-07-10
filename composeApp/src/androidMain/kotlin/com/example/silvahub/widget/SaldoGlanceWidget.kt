package com.example.silvahub.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.silvahub.domain.usecase.ObterResumoFinanceiroUseCase
import com.example.silvahub.util.MainActivity
import com.example.silvahub.util.MoneyFormat
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext

class SaldoGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val saldo = runCatching {
            val useCase = GlobalContext.get().get<ObterResumoFinanceiroUseCase>()
            useCase().first().saldoDisponivel
        }.getOrDefault(0.0)

        provideContent {
            GlanceTheme {
                WidgetContent(saldo = saldo)
            }
        }
    }
}

private val openNovoGastoKey = ActionParameters.Key<Boolean>("open_novo_gasto")

@Composable
private fun WidgetContent(saldo: Double) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.primaryContainer)
            .padding(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(openNovoGastoKey to true),
                ),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "Saldo do mês", style = TextStyle(fontSize = 14.sp))
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = MoneyFormat.format(saldo),
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(text = "Toque para + Gasto", style = TextStyle(fontSize = 12.sp))
    }
}

class SaldoGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SaldoGlanceWidget()
}
