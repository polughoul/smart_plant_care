package com.example.smart_plant_care.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LeafGreenDark,
    onPrimary = SoilLight,
    primaryContainer = LeafTintDark,
    onPrimaryContainer = SoilDark,
    secondary = MossGreenDark,
    onSecondary = SoilLight,
    secondaryContainer = StemLight,
    onSecondaryContainer = SoilDark,
    tertiary = BlossomDark,
    onTertiary = SoilLight,
    background = SeedlingDark,
    onBackground = SoilDark,
    surface = SeedlingDark,
    onSurface = SoilDark,
    surfaceVariant = LeafTintDark,
    onSurfaceVariant = SoilDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = LeafGreenLight,
    onPrimary = SeedlingLight,
    primaryContainer = LeafTintLight,
    onPrimaryContainer = SoilLight,
    secondary = MossGreenLight,
    onSecondary = SeedlingLight,
    secondaryContainer = LeafGreenDark,
    onSecondaryContainer = SoilLight,
    tertiary = BlossomLight,
    onTertiary = SeedlingLight,
    background = SeedlingLight,
    onBackground = SoilLight,
    surface = SeedlingLight,
    onSurface = SoilLight,
    surfaceVariant = LeafTintLight,
    onSurfaceVariant = StemLight,
    outline = OutlineLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Smart_Plant_CareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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