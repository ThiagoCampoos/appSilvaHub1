package com.example.silvahub

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silvahub.data.preferences.ThemeMode
import com.example.silvahub.data.preferences.UserPreferencesRepository
import com.example.silvahub.ui.navigation.AppNavHost
import com.example.silvahub.ui.theme.SilvaHubTheme
import org.koin.compose.koinInject

@Composable
fun App(openNovoGasto: Boolean = false) {
    val preferences: UserPreferencesRepository = koinInject()
    val themeMode by preferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    SilvaHubTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppNavHost(openNovoGasto = openNovoGasto)
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    SilvaHubTheme {
        Surface { }
    }
}
