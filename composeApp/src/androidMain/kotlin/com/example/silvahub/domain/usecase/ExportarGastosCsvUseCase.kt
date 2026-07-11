package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.repository.GastoRepository
import com.example.silvahub.util.DateUtils
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportarGastosCsvUseCase(
    private val gastoRepository: GastoRepository,
) {
    suspend operator fun invoke(mesAno: String = DateUtils.mesReferenciaAtual()): String {
        val gastos = gastoRepository.getGastoDoMes(mesAno).first()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val header = "id;descricao;valor;categoria;data;tipo"
        val lines = gastos.map { gasto ->
            listOf(
                gasto.id.toString(),
                "\"${gasto.descricao.replace("\"", "\"\"")}\"",
                gasto.valor.toString().replace('.', ','),
                gasto.categoria.name,
                dateFormat.format(Date(gasto.data)),
                gasto.tipo.name,
            ).joinToString(";")
        }
        return (listOf(header) + lines).joinToString("\n")
    }
}
