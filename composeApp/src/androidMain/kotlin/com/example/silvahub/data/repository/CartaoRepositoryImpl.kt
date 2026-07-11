package com.example.silvahub.data.repository

import com.example.silvahub.data.local.dao.CartaoDao
import com.example.silvahub.data.local.dao.FaturaDao
import com.example.silvahub.data.local.dao.PagamentoFaturaDao
import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.ResumoLimite
import com.example.silvahub.domain.repository.CartaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class CartaoRepositoryImpl(
    private val cartaoDao: CartaoDao,
    private val faturaDao: FaturaDao,
    private val pagamentoFaturaDao: PagamentoFaturaDao,
) : CartaoRepository {

    override suspend fun salvar(cartao: Cartao): Long {
        return cartaoDao.inserir(cartao.toEntity())
    }

    override suspend fun atualizar(cartao: Cartao) {
        cartaoDao.atualizar(cartao.toEntity())
    }

    override fun getUnico(): Flow<Cartao?> {
        return cartaoDao.getUnico().map { it?.toDomain() }
    }

    override suspend fun getUnicoOnce(): Cartao? {
        return cartaoDao.getUnicoOnce()?.toDomain()
    }

    override fun getResumoLimite(cartaoId: Long): Flow<ResumoLimite> {
        return combine(
            cartaoDao.getPorId(cartaoId),
            faturaDao.somaTodasParcelasDoCartao(cartaoId),
            pagamentoFaturaDao.somaPagamentosAtivosDoCartao(cartaoId),
        ) { cartao, somaParcelas, somaPagamentos ->
            val limite = cartao?.limiteCentavos ?: 0L
            val utilizado = somaParcelas - somaPagamentos
            ResumoLimite(
                limiteTotalCentavos = limite,
                limiteUtilizadoCentavos = utilizado,
                limiteDisponivelCentavos = limite - utilizado,
            )
        }
    }

    override suspend fun getResumoLimiteOnce(cartaoId: Long): ResumoLimite {
        val cartao = cartaoDao.getPorIdOnce(cartaoId)
        val limite = cartao?.limiteCentavos ?: 0L
        val somaParcelas = faturaDao.somaTodasParcelasDoCartaoOnce(cartaoId)
        val somaPagamentos = pagamentoFaturaDao.somaPagamentosAtivosDoCartaoOnce(cartaoId)
        val utilizado = somaParcelas - somaPagamentos
        return ResumoLimite(
            limiteTotalCentavos = limite,
            limiteUtilizadoCentavos = utilizado,
            limiteDisponivelCentavos = limite - utilizado,
        )
    }
}
