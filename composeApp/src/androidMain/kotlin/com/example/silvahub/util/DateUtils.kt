package com.example.silvahub.util

import java.util.Calendar

object DateUtils {
    fun mesReferenciaAtual(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return "%04d-%02d".format(year, month)
    }

    fun mesAnoToRange(mesAno: String): Pair<Long, Long> {
        val parts = mesAno.split("-")
        require(parts.size == 2) { "mesAno inválido: $mesAno" }
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val startCal = Calendar.getInstance().apply {
            clear()
            set(year, month - 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            timeInMillis = startCal.timeInMillis
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
        return startCal.timeInMillis to endCal.timeInMillis
    }

    fun addMonths(timestamp: Long, months: Int): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        cal.add(Calendar.MONTH, months)
        return cal.timeInMillis
    }

    fun mesAnoFromTimestamp(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return "%04d-%02d".format(year, month)
    }

    fun previousMesAno(mesAno: String): String {
        val parts = mesAno.split("-")
        val cal = Calendar.getInstance().apply {
            clear()
            set(parts[0].toInt(), parts[1].toInt() - 1, 1)
            add(Calendar.MONTH, -1)
        }
        return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun nextMesAno(mesAno: String): String {
        val parts = mesAno.split("-")
        val cal = Calendar.getInstance().apply {
            clear()
            set(parts[0].toInt(), parts[1].toInt() - 1, 1)
            add(Calendar.MONTH, 1)
        }
        return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun daysRemainingInMonth(): Int {
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_MONTH)
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (lastDay - today + 1).coerceAtLeast(1)
    }

    fun clampDiaCartao(dia: Int): Int = dia.coerceIn(1, 28)


    fun mesReferenciaFatura(dataCompra: Long, diaFechamento: Int): String {
        val dia = clampDiaCartao(diaFechamento)
        val cal = Calendar.getInstance().apply { timeInMillis = dataCompra }
        val diaCompra = cal.get(Calendar.DAY_OF_MONTH)
        val mesAno = mesAnoFromTimestamp(dataCompra)
        return if (diaCompra >= dia) nextMesAno(mesAno) else mesAno
    }

    fun dataFechamentoNoMes(mesAno: String, diaFechamento: Int): Long {
        return timestampNoMes(mesAno, clampDiaCartao(diaFechamento))
    }

    fun dataVencimentoNoMes(mesAno: String, diaVencimento: Int): Long {
        return timestampNoMes(mesAno, clampDiaCartao(diaVencimento))
    }

    private fun timestampNoMes(mesAno: String, dia: Int): Long {
        val parts = mesAno.split("-")
        require(parts.size == 2) { "mesAno inválido: $mesAno" }
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        return Calendar.getInstance().apply {
            clear()
            set(year, month - 1, dia, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun addMonthsToMesAno(mesAno: String, months: Int): String {
        var result = mesAno
        if (months >= 0) {
            repeat(months) { result = nextMesAno(result) }
        } else {
            repeat(-months) { result = previousMesAno(result) }
        }
        return result
    }
}
