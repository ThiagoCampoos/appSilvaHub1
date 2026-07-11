package com.example.silvahub.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {
    @Test
    fun toCentavosArredondaHalfEven() {
        assertEquals(1000L, Money.toCentavos(10.0))
        assertEquals(1050L, Money.toCentavos(10.5))
        assertEquals(1L, Money.toCentavos(0.01))
    }

    @Test
    fun fromCentavos() {
        assertEquals(10.0, Money.fromCentavos(1000L), 0.0001)
        assertEquals(33.34, Money.fromCentavos(3334L), 0.0001)
    }

    @Test
    fun dividirParcelasDistribuiResto() {
        // 100,00 em 3 → 33,34 + 33,33 + 33,33
        val parcelas = Money.dividirParcelas(10000L, 3)
        assertEquals(listOf(3334L, 3333L, 3333L), parcelas)
        assertEquals(10000L, parcelas.sum())
    }

    @Test
    fun dividirParcelasExatas() {
        val parcelas = Money.dividirParcelas(10000L, 2)
        assertEquals(listOf(5000L, 5000L), parcelas)
    }
}
