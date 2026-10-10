package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = IkibinaBrightBlue,
    onPrimary = Color.White,
    primaryContainer = IkibinaBlueLight,
    onPrimaryContainer = IkibinaBlueDeep,
    secondary = IkibinaYellow,
    onSecondary = IkibinaDarkGray,
    secondaryContainer = IkibinaYellowLight,
    onSecondaryContainer = IkibinaYellowDeep,
    tertiary = IkibinaYellowDark,
    onTertiary = Color.White,
    tertiaryContainer = IkibinaYellowLight,
    onTertiaryContainer = IkibinaYellowDeep,
    background = IkibinaBackground,
    onBackground = IkibinaDarkGray,
    surface = IkibinaCardBg,
    onSurface = IkibinaDarkGray,
    surfaceVariant = IkibinaGray100,
    onSurfaceVariant = IkibinaGray600,
    outline = IkibinaGray200,
    error = IkibinaRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = IkibinaBrightBlue,
    onPrimary = Color.White,
    primaryContainer = IkibinaBlueDark,
    onPrimaryContainer = Color.White,
    secondary = IkibinaYellow,
    onSecondary = IkibinaDarkGray,
    secondaryContainer = IkibinaSurfaceDark,
    onSecondaryContainer = Color.White,
    tertiary = IkibinaYellowLight,
    onTertiary = IkibinaDarkGray,
    background = IkibinaDarkGray,
    onBackground = Color.White,
    surface = IkibinaSurfaceDark,
    onSurface = Color.White,
    surfaceVariant = IkibinaDarkGray,
    onSurfaceVariant = IkibinaGray400,
    outline = IkibinaGray600,
    error = IkibinaRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded financial colors: Bright Blue, Yellow, White
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
