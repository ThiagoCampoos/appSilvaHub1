package com.example.silvahub.domain.model

enum class ECategoriaGasto {
    ALIMENTACAO,
    TRANSPORTE,
    LAZER,
    SAUDE,
    GREEN,
    BESTEIRA,
    BEBIDA,
    OUTROS,
}

fun ECategoriaGasto.label(): String = when (this) {
    ECategoriaGasto.ALIMENTACAO -> "Alimentação"
    ECategoriaGasto.TRANSPORTE -> "Transporte"
    ECategoriaGasto.LAZER -> "Lazer"
    ECategoriaGasto.SAUDE -> "Saúde"
    ECategoriaGasto.GREEN -> "Green"
    ECategoriaGasto.BESTEIRA -> "Comida"
    ECategoriaGasto.BEBIDA -> "Bebida"
    ECategoriaGasto.OUTROS -> "Outros"
}
