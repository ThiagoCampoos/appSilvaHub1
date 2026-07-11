package com.example.silvahub.util

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToLong

/**
 * Conversão monetária Double ↔ centavos (Long).
 * Arredondamento half-even na conversão Double → centavos.
 * Divisão de parcelas distribui o resto nas primeiras parcelas (+1 centavo).
 */
object Money {
    fun toCentavos(value: Double): Long {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_EVEN)
            .movePointRight(2)
            .longValueExact()
    }

    fun fromCentavos(centavos: Long): Double = centavos / 100.0

    /**
     * Divide [totalCentavos] em [n] parcelas.
     * Ex.: 10000 em 3 → [3334, 3333, 3333]
     */
    fun dividirParcelas(totalCentavos: Long, n: Int): List<Long> {
        require(n > 0) { "Número de parcelas deve ser > 0" }
        require(totalCentavos >= 0) { "Valor total deve ser >= 0" }
        val base = totalCentavos / n
        val resto = (totalCentavos % n).toInt()
        return List(n) { i ->
            if (i < resto) base + 1 else base
        }
    }

    fun parseInputToCentavos(input: String): Long? {
        val normalized = input.trim()
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")
            .replace(".", "")
            .replace(",", ".")
        val value = normalized.toDoubleOrNull() ?: return null
        return toCentavos(value)
    }
}
