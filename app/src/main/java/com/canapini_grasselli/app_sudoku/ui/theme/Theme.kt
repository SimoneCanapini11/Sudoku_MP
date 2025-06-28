package com.canapini_grasselli.app_sudoku.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.canapini_grasselli.app_sudoku.model.AppTheme

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color.Black,
    secondary = PurpleGrey80,
    onSecondary = Color.Black,
    tertiary = Pink80,
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    primaryContainer = Color(0xFF4D3D7F),
    onSurface = Color.White,
    secondaryContainer = Color(0xFF85C1E9)
)

private val PurpleLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Color.Black,
    secondary = Green80,
    onSecondary = Color.Black,
    tertiary = Green80,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val GreenLightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = Green40,
    tertiary = Green40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun App_SudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    appTheme: AppTheme = AppTheme.PURPLE,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> when (appTheme) {
            AppTheme.PURPLE -> PurpleDarkColorScheme
            AppTheme.GREEN -> GreenDarkColorScheme
        }
        else -> when (appTheme) {
            AppTheme.PURPLE -> PurpleLightColorScheme
            AppTheme.GREEN -> GreenLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}