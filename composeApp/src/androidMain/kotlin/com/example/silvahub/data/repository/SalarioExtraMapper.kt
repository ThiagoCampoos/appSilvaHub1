package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.SalarioExtraEntity
import com.example.silvahub.domain.model.SalarioExtra

fun SalarioExtraEntity.toDomain() = SalarioExtra(
    id = id,
    descricao = descricao,
    valor = valor,
    mesReferencia = mesReferencia,
    dataCriacao = dataCriacao,
)

fun SalarioExtra.toEntity() = SalarioExtraEntity(
    id = id,
    descricao = descricao,
    valor = valor,
    mesReferencia = mesReferencia,
    dataCriacao = dataCriacao,
)
