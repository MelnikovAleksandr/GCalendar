package ru.melnikov.gcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun GCalendarTheme(
    shapes: Shapes = GCalendarTheme.shapes,
    typography: Typography = GCalendarTheme.typography,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    CompositionLocalProvider {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

object GCalendarTheme {
    val dimensions: Dimensions
        @Composable @ReadOnlyComposable get() = Dimensions

    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = Typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = AppShapes
}