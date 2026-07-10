package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.domain.model.Gasto

object GastoMapper {
    fun toDomain(entity: GastoEntity) = Gasto(
        id = entity.id,
        descricao = entity.descricao,
        valor = entity.valor,
        categoria = entity.categoria,
        data = entity.data,
        tipo = entity.tipo,
        parcelaAtual = entity.parcelaAtual,
        totalParcelas = entity.totalParcelas,
        grupoParcelamentoId = entity.grupoParcelamentoId,
        dataCriacao = entity.dataCriacao,
    )

    fun toEntity(domain: Gasto) = GastoEntity(
        id = domain.id,
        descricao = domain.descricao,
        valor = domain.valor,
        categoria = domain.categoria,
        data = domain.data,
        tipo = domain.tipo,
        parcelaAtual = domain.parcelaAtual,
        totalParcelas = domain.totalParcelas,
        grupoParcelamentoId = domain.grupoParcelamentoId,
        dataCriacao = domain.dataCriacao,
    )
}
