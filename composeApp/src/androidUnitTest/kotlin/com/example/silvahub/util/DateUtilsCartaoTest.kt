package com.example.silvahub.util

import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals

class DateUtilsCartaoTest {
    @Test
    fun clampDiaCartao() {
        assertEquals(1, DateUtils.clampDiaCartao(0))
        assertEquals(28, DateUtils.clampDiaCartao(31))
        assertEquals(15, DateUtils.clampDiaCartao(15))
    }

    @Test
    fun mesReferenciaFaturaAntesDoFechamento() {
        val cal = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 5, 12, 0, 0)
        }
        assertEquals("2026-07", DateUtils.mesReferenciaFatura(cal.timeInMillis, 10))
    }

    @Test
    fun mesReferenciaFaturaNoFechamentoVaiParaProximo() {
        val cal = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 10, 12, 0, 0)
        }
        assertEquals("2026-08", DateUtils.mesReferenciaFatura(cal.timeInMillis, 10))
    }

    @Test
    fun addMonthsToMesAno() {
        assertEquals("2026-09", DateUtils.addMonthsToMesAno("2026-07", 2))
        assertEquals("2026-05", DateUtils.addMonthsToMesAno("2026-07", -2))
    }
}
