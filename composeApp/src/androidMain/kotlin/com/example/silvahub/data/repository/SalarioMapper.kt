package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.SalarioEntity
import com.example.silvahub.domain.model.Salario

fun SalarioEntity.toDomain(): Salario {
    return Salario(
        id = id,
        valor = valor,
        mesReferencia = mesReferencia,
        dataCriacao = dataCriacao,
    )
}

fun Salario.toEntity(): SalarioEntity {
    return SalarioEntity(
        id = id,
        valor = valor,
        mesReferencia = mesReferencia,
        dataCriacao = dataCriacao,
    )
}

