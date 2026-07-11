package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.PagamentoFatura
import com.example.silvahub.domain.repository.FaturaRepository
import com.example.silvahub.domain.repository.PagamentoFaturaRepository

class PagarFaturaUseCase(
    private val faturaRepository: FaturaRepository,
    private val pagamentoFaturaRepository: PagamentoFaturaRepository,
) {
    /**
     * Registra pagamento (parcial, total ou antecipado).
     * Debita o saldo em conta indiretamente via ObterResumoFinanceiroUseCase
     * (soma de pagamentos do mês). Não bloqueia saldo negativo.
     */
    suspend operator fun invoke(
        faturaId: Long,
        valorCentavos: Long,
        data: Long = System.currentTimeMillis(),
    ): Long {
        require(valorCentavos > 0) { "Valor do pagamento deve ser maior que zero" }

        return faturaRepository.withTransaction {
            val fatura = faturaRepository.getFaturaPorIdOnce(faturaId)
                ?: error("Fatura não encontrada")
            val total = faturaRepository.somaParcelasDaFaturaOnce(faturaId)
            val pendente = (total - fatura.valorPagoCentavos).coerceAtLeast(0)
            require(valorCentavos <= pendente) {
                "Pagamento ($valorCentavos) excede o saldo pendente ($pendente)"
            }

            val pagamentoId = pagamentoFaturaRepository.salvar(
                PagamentoFatura(
                    faturaId = faturaId,
                    valorCentavos = valorCentavos,
                    data = data,
                ),
            )

            val novoPago = fatura.valorPagoCentavos + valorCentavos
            val novoStatus = if (novoPago >= total && total > 0) {
                EStatusFatura.PAGA
            } else {
                EStatusFatura.ABERTA
            }
            faturaRepository.atualizarFatura(
                fatura.copy(
                    valorPagoCentavos = novoPago,
                    status = novoStatus,
                ),
            )
            pagamentoId
        }
    }
}

class EstornarPagamentoUseCase(
    private val faturaRepository: FaturaRepository,
    private val pagamentoFaturaRepository: PagamentoFaturaRepository,
) {
    suspend operator fun invoke(pagamentoId: Long) {
        faturaRepository.withTransaction {
            val pagamento = pagamentoFaturaRepository.getPorIdOnce(pagamentoId)
                ?: error("Pagamento não encontrado")
            require(!pagamento.estornado) { "Pagamento já estornado" }

            val fatura = faturaRepository.getFaturaPorIdOnce(pagamento.faturaId)
                ?: error("Fatura não encontrada")

            pagamentoFaturaRepository.atualizar(
                pagamento.copy(
                    estornado = true,
                    dataEstorno = System.currentTimeMillis(),
                ),
            )

            val novoPago = (fatura.valorPagoCentavos - pagamento.valorCentavos).coerceAtLeast(0)
            faturaRepository.atualizarFatura(
                fatura.copy(
                    valorPagoCentavos = novoPago,
                    status = EStatusFatura.ABERTA,
                ),
            )
        }
    }
}
