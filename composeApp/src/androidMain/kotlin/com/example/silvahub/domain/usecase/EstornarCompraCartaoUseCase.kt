package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.domain.repository.FaturaRepository

class EstornarCompraCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
    private val faturaRepository: FaturaRepository,
    private val registrarCompraCartaoUseCase: RegistrarCompraCartaoUseCase,
) {
    suspend operator fun invoke(compraId: Long) {
        val compra = faturaRepository.getCompraPorIdOnce(compraId)
            ?: error("Compra não encontrada")
        require(!compra.estornada) { "Compra já estornada" }
        require(compra.tipo != ETipoCompraCartao.AJUSTE_ESTORNO) {
            "Não é possível estornar um ajuste"
        }

        faturaRepository.withTransaction {
            val parcelas = faturaRepository.getParcelasDaCompra(compraId)
            val parcelasPagas = mutableListOf<com.example.silvahub.domain.model.ParcelaCartao>()
            val parcelasAbertas = mutableListOf<com.example.silvahub.domain.model.ParcelaCartao>()

            for (parcela in parcelas) {
                val status = faturaRepository.getFaturaPorIdOnce(parcela.faturaId)?.status
                if (status == EStatusFatura.PAGA) {
                    parcelasPagas += parcela
                } else {
                    parcelasAbertas += parcela
                }
            }

            if (parcelasPagas.isEmpty()) {
                faturaRepository.deletarParcelasDaCompra(compraId)
                faturaRepository.atualizarCompra(compra.copy(estornada = true))
            } else {
                // Mantém parcelas de faturas pagas; remove as de faturas abertas
                faturaRepository.deletarParcelasDaCompra(compraId)
                if (parcelasPagas.isNotEmpty()) {
                    faturaRepository.inserirParcelas(parcelasPagas)
                }
                faturaRepository.atualizarCompra(compra.copy(estornada = true))

                // Ajuste negativo libera o limite das parcelas já pagas
                val valorAjuste = parcelasPagas.sumOf { it.valorCentavos }
                if (valorAjuste > 0) {
                    registrarCompraCartaoUseCase(
                        descricao = "Estorno: ${compra.descricao}",
                        valorCentavos = valorAjuste,
                        categoria = compra.categoria,
                        data = System.currentTimeMillis(),
                        tipo = ETipoCompraCartao.AJUSTE_ESTORNO,
                    )
                }
            }
        }
    }
}
