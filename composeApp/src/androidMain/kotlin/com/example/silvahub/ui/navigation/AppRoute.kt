package com.example.silvahub.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Gastos : AppRoute

    @Serializable
    data object Historico : AppRoute

    @Serializable
    data object Configuracoes : AppRoute

    @Serializable
    data object Graficos : AppRoute

    @Serializable
    data object Cartao : AppRoute

    @Serializable
    data class DetalhesGasto(val gastoId: Long) : AppRoute

    @Serializable
    data class DetalhesFatura(val faturaId: Long) : AppRoute

    @Serializable
    data class DetalhesCompraCartao(val compraId: Long) : AppRoute

    @Serializable
    data class EditarContaFixa(val contaId: Long) : AppRoute
}
