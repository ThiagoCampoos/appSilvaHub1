package com.example.silvahub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = SecondaryRed,
    onSecondary = SurfaceLight,
    secondaryContainer = SecondaryRedLight,
    onSecondaryContainer = SecondaryRedDark,
    tertiary = StatusInfo,
    onTertiary = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    error = StatusError,
    onError = SurfaceLight,
    errorContainer = SecondaryRedLight,
    onErrorContainer = SecondaryRedDark,
    outline = TextSecondary
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = PrimaryGreenDark,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryGreenLight,
    secondary = SecondaryRedLight,
    onSecondary = SecondaryRedDark,
    secondaryContainer = SecondaryRedDark,
    onSecondaryContainer = SecondaryRedLight,
    tertiary = StatusInfo,
    onTertiary = SurfaceLight,
    background = BackgroundDark,
    onBackground = BackgroundLight,
    surface = SurfaceDark,
    onSurface = BackgroundLight,
    surfaceVariant = BackgroundDark,
    onSurfaceVariant = TextTertiary,
    error = StatusError,
    onError = SurfaceLight,
    errorContainer = SecondaryRedDark,
    onErrorContainer = SecondaryRedLight,
    outline = TextTertiary
)

@Composable
fun SilvaHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SilvaHubTypography,
        content = content
    )

}
