package com.example.silvahub.util

import java.text.NumberFormat
import java.util.Locale

object MoneyFormat {
    private val brl: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun format(value: Double): String = brl.format(value)

    fun formatCentavos(centavos: Long): String = format(Money.fromCentavos(centavos))
}
