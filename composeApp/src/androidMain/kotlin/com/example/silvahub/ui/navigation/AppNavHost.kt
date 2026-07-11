package com.example.silvahub.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.silvahub.ui.components.SilvaHubLogo
import com.example.silvahub.ui.screens.configuracoes.ConfiguracoesScreen
import com.example.silvahub.ui.screens.configuracoes.EditarContaFixaScreen
import com.example.silvahub.ui.screens.gastos.DetalhesGastoScreen
import com.example.silvahub.ui.screens.gastos.GastosScreen
import com.example.silvahub.ui.screens.graficos.GraficosScreen
import com.example.silvahub.ui.screens.historico.HistoricoScreen
import com.example.silvahub.ui.screens.home.HomeScreen

private data class TopLevelRoute(
    val label: String,
    val route: AppRoute,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val useBrandLogo: Boolean = false,
)

@Composable
fun AppNavHost(openNovoGasto: Boolean = false) {
    val navController = rememberNavController()
    val topLevel = listOf(
        TopLevelRoute("Home", AppRoute.Home, Icons.Filled.Home, Icons.Outlined.Home),
        TopLevelRoute("Gastos", AppRoute.Gastos, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        TopLevelRoute(
            "Histórico",
            AppRoute.Historico,
            Icons.AutoMirrored.Filled.List,
            Icons.AutoMirrored.Outlined.List,
        ),
        TopLevelRoute("Config", AppRoute.Configuracoes, useBrandLogo = true),
    )

    LaunchedEffect(openNovoGasto) {
        if (openNovoGasto) {
            navController.navigate(AppRoute.Gastos) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = topLevel.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevel.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (item.useBrandLogo) {
                                    SilvaHubLogo(size = 28.dp, circular = true)
                                } else {
                                    Icon(
                                        imageVector = if (selected) {
                                            item.selectedIcon!!
                                        } else {
                                            item.unselectedIcon!!
                                        },
                                        contentDescription = item.label,
                                    )
                                }
                            },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Home,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn() + slideInHorizontally { it / 8 } },
            exitTransition = { fadeOut() + slideOutHorizontally { -it / 8 } },
        ) {
            composable<AppRoute.Home> {
                HomeScreen(
                    onOpenGastos = {
                        navController.navigate(AppRoute.Gastos) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenGraficos = { navController.navigate(AppRoute.Graficos) },
                    onOpenConfiguracoes = {
                        navController.navigate(AppRoute.Configuracoes) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable<AppRoute.Gastos> {
                GastosScreen(
                    openSheetOnStart = openNovoGasto,
                    onOpenDetalhe = { id -> navController.navigate(AppRoute.DetalhesGasto(id)) },
                )
            }
            composable<AppRoute.Historico> {
                HistoricoScreen(
                    onOpenDetalhe = { id -> navController.navigate(AppRoute.DetalhesGasto(id)) },
                )
            }
            composable<AppRoute.Configuracoes> {
                ConfiguracoesScreen(
                    onEditConta = { id -> navController.navigate(AppRoute.EditarContaFixa(id)) },
                )
            }
            composable<AppRoute.Graficos> {
                GraficosScreen(onBack = { navController.popBackStack() })
            }
            composable<AppRoute.DetalhesGasto> { entry ->
                val route = entry.toRoute<AppRoute.DetalhesGasto>()
                DetalhesGastoScreen(
                    gastoId = route.gastoId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable<AppRoute.EditarContaFixa> { entry ->
                val route = entry.toRoute<AppRoute.EditarContaFixa>()
                EditarContaFixaScreen(
                    contaId = route.contaId,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
