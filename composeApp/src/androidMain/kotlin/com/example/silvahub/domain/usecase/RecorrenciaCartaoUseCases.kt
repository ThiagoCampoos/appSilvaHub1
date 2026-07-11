package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.RecorrenciaCartao
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.util.DateUtils
import java.util.Calendar

class CriarRecorrenciaCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
    private val registrarCompraCartaoUseCase: RegistrarCompraCartaoUseCase,
) {
    suspend operator fun invoke(
        descricao: String,
        valorCentavos: Long,
        categoria: com.example.silvahub.domain.model.ECategoriaGasto,
        diaCobranca: Int,
        dataInicio: Long = System.currentTimeMillis(),
    ): Long {
        require(descricao.isNotBlank()) { "Descrição obrigatória" }
        require(valorCentavos > 0) { "Valor deve ser maior que zero" }
        val cartao = cartaoRepository.getUnicoOnce()
            ?: error("Configure o cartão de crédito antes")
        val dia = DateUtils.clampDiaCartao(diaCobranca)

        val recorrenciaId = faturaRepository.salvarRecorrencia(
            RecorrenciaCartao(
                cartaoId = cartao.id,
                descricao = descricao,
                valorCentavos = valorCentavos,
                categoria = categoria,
                diaCobranca = dia,
                ativa = true,
                dataInicio = dataInicio,
            ),
        )

        // Gera cobrança do mês atual se aplicável
        GerarCobrancasRecorrentesUseCase(
            cartaoRepository,
            faturaRepository,
            registrarCompraCartaoUseCase,
        ).invoke()

        return recorrenciaId
    }
}

class CancelarRecorrenciaCartaoUseCase(
    private val faturaRepository: FaturaRepository,
) {
    suspend operator fun invoke(recorrenciaId: Long) {
        val recorrencia = faturaRepository.getRecorrenciaPorIdOnce(recorrenciaId)
            ?: error("Recorrência não encontrada")
        faturaRepository.atualizarRecorrencia(
            recorrencia.copy(
                ativa = false,
                dataCancelamento = System.currentTimeMillis(),
            ),
        )
    }
}

/**
 * Gera cobranças do mês corrente para todas as recorrências ativas.
 * Idempotente: índice único (recorrencia_id, mes_referencia_cobranca) impede duplicidade.
 */
class GerarCobrancasRecorrentesUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
    private val registrarCompraCartaoUseCase: RegistrarCompraCartaoUseCase,
) {
    suspend operator fun invoke(agora: Long = System.currentTimeMillis()) {
        val cartao = cartaoRepository.getUnicoOnce() ?: return
        val mesAtual = DateUtils.mesAnoFromTimestamp(agora)
        val recorrencias = faturaRepository.getRecorrenciasAtivasOnce(cartao.id)

        for (rec in recorrencias) {
            val existente = faturaRepository.getCompraRecorrenteDoMes(rec.id, mesAtual)
            if (existente != null) continue

            val dataInicioMes = DateUtils.mesAnoFromTimestamp(rec.dataInicio)
            if (mesAtual < dataInicioMes) continue

            val cal = Calendar.getInstance().apply { timeInMillis = agora }
            val diaHoje = cal.get(Calendar.DAY_OF_MONTH)
            // Só gera se o dia de cobrança já passou ou é hoje
            if (diaHoje < rec.diaCobranca) continue

            val dataCobranca = Calendar.getInstance().apply {
                clear()
                val parts = mesAtual.split("-")
                set(parts[0].toInt(), parts[1].toInt() - 1, rec.diaCobranca, 12, 0, 0)
            }.timeInMillis

            try {
                registrarCompraCartaoUseCase(
                    descricao = rec.descricao,
                    valorCentavos = rec.valorCentavos,
                    categoria = rec.categoria,
                    data = dataCobranca,
                    tipo = ETipoCompraCartao.CREDITO_RECORRENTE,
                    totalParcelas = 1,
                    recorrenciaId = rec.id,
                    mesReferenciaCobranca = mesAtual,
                )
            } catch (_: Exception) {
                // Limite insuficiente ou duplicidade — ignora e tenta na próxima execução
            }
        }
    }
}
