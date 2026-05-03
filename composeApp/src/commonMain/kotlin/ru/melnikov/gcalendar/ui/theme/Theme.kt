package ru.melnikov.gcalendar.ui.theme

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme

val LocalSharedTransitionScope =
    compositionLocalOf<SharedTransitionScope> {
        throw IllegalStateException("No SharedTransitionScope provided")
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GCalendarTheme(
    shapes: Shapes = GCalendarTheme.shapes,
    typography: Typography = GCalendarTheme.typography,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        rememberDynamicColorScheme(
            Color(0xFF4285F4),
            useDarkTheme,
            isAmoled = true,
        )
    CompositionLocalProvider {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = {
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        content()
                    }
                }
            },
            motionScheme = MotionScheme.expressive(),
        )
    }
}

object GCalendarTheme {
    val dimensions: Dimensions
        @Composable @ReadOnlyComposable
        get() = Dimensions

    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = Typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable
        get() = AppShapes
}