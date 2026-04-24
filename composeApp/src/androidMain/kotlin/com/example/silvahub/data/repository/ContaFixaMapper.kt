package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.ContaFixaEntity
import com.example.silvahub.domain.model.ContaFixa

fun ContaFixaEntity.toDomain(): ContaFixa {
    return ContaFixa(
        id = id,
        nome = nome,
        valor = valor,
        diaVencimento = diaVencimento,
        ativa = ativa,
        dataCriacao = dataCriacao,
    )
}

fun ContaFixa.toEntity(): ContaFixaEntity {
    return ContaFixaEntity(
        id = id,
        nome = nome,
        valor = valor,
        diaVencimento = diaVencimento,
        ativa = ativa,
        dataCriacao = dataCriacao,
    )
}

