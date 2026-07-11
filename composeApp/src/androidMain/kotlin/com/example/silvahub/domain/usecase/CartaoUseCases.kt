package com.example.silvahub.domain.usecase

import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.repository.CartaoRepository
import com.example.silvahub.util.DateUtils

data class SalvarCartaoResultado(
    val cartaoId: Long,
    val avisoLimiteAbaixoUtilizado: Boolean = false,
)

class SalvarCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
) {
    suspend operator fun invoke(
        limiteCentavos: Long,
        diaFechamento: Int,
        diaVencimento: Int,
    ): SalvarCartaoResultado {
        require(limiteCentavos > 0) { "Limite deve ser maior que zero" }
        val fechamento = DateUtils.clampDiaCartao(diaFechamento)
        val vencimento = DateUtils.clampDiaCartao(diaVencimento)
        require(fechamento in 1..28) { "Dia de fechamento inválido" }
        require(vencimento in 1..28) { "Dia de vencimento inválido" }

        val existente = cartaoRepository.getUnicoOnce()
        val cartao = Cartao(
            id = Cartao.CARTAO_UNICO_ID,
            limiteCentavos = limiteCentavos,
            diaFechamento = fechamento,
            diaVencimento = vencimento,
            dataCriacao = existente?.dataCriacao ?: System.currentTimeMillis(),
        )

        var aviso = false
        if (existente != null) {
            val resumo = cartaoRepository.getResumoLimiteOnce(Cartao.CARTAO_UNICO_ID)
            aviso = limiteCentavos < resumo.limiteUtilizadoCentavos
            cartaoRepository.atualizar(cartao)
        } else {
            cartaoRepository.salvar(cartao)
        }
        return SalvarCartaoResultado(Cartao.CARTAO_UNICO_ID, aviso)
    }
}

class ObterCartaoUseCase(
    private val cartaoRepository: CartaoRepository,
) {
    operator fun invoke() = cartaoRepository.getUnico()
}

class ObterResumoLimiteUseCase(
    private val cartaoRepository: CartaoRepository,
) {
    operator fun invoke(cartaoId: Long = Cartao.CARTAO_UNICO_ID) =
        cartaoRepository.getResumoLimite(cartaoId)
}
