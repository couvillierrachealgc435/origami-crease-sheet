package com.crease.origamisheet.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = PaperSurface,
    primaryContainer = CrimsonSoft,
    onPrimaryContainer = PaperSurface,
    secondary = Teal,
    onSecondary = PaperSurface,
    secondaryContainer = TealSoft,
    onSecondaryContainer = Ink,
    tertiary = Gold,
    onTertiary = Ink,
    background = PaperBackground,
    onBackground = Ink,
    surface = PaperSurface,
    onSurface = Ink,
    surfaceVariant = PaperSurfaceAlt,
    onSurfaceVariant = InkMuted,
    outline = PaperEdge,
    outlineVariant = PaperEdge,
    error = Crimson,
    onError = PaperSurface,
    scrim = Ink
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
