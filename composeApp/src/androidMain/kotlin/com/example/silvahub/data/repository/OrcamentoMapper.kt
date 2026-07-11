package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.OrcamentoEntity
import com.example.silvahub.domain.model.Orcamento

fun OrcamentoEntity.toDomain() = Orcamento(
    id = id,
    categoria = categoria,
    limiteMensal = limiteMensal,
    ativo = ativo,
    dataCriacao = dataCriacao,
)

fun Orcamento.toEntity() = OrcamentoEntity(
    id = id,
    categoria = categoria,
    limiteMensal = limiteMensal,
    ativo = ativo,
    dataCriacao = dataCriacao,
)
