package org.example.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.example.project.presentation.navigation.AppNavigation

// ── Hijau (hanya untuk aksen/tombol) ──────────────────────────────────────
private val GreenPrimary     = Color(0xFF2E7D32)  // hijau tua → tombol, icon aktif
private val GreenLight       = Color(0xFF4CAF50)  // hijau medium
private val GreenContainer   = Color(0xFFDCEDC8)  // hijau sangat muda → chip/badge

private val LightColors = lightColorScheme(
    primary              = GreenPrimary,
    onPrimary            = Color.White,
    primaryContainer     = GreenContainer,
    onPrimaryContainer   = Color(0xFF1B5E20),

    secondary            = GreenLight,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFF1F8E9),     // putih kehijau sangat tipis
    onSecondaryContainer = Color(0xFF33691E),

    // Background & Surface → PUTIH bersih
    background           = Color(0xFFFFFFFF),      // putih
    onBackground         = Color(0xFF1A1A1A),
    surface              = Color(0xFFFFFFFF),      // putih
    onSurface            = Color(0xFF1A1A1A),

    // surfaceVariant → abu muda (bukan hijau!) untuk bubble chat AI
    surfaceVariant       = Color(0xFFF0F0F0),      // abu muda netral
    onSurfaceVariant     = Color(0xFF424242),

    // TopAppBar pakai abu muda bukan hijau
    surfaceContainer     = Color(0xFFF5F5F5),

    error                = Color(0xFFB00020),
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF8B0000),
)

private val DarkColors = darkColorScheme(
    primary              = GreenLight,
    onPrimary            = Color(0xFF003300),
    primaryContainer     = Color(0xFF1B5E20),
    onPrimaryContainer   = Color(0xFFDCEDC8),

    secondary            = Color(0xFF81C784),
    onSecondary          = Color(0xFF003300),

    background           = Color(0xFF121212),
    onBackground         = Color(0xFFE0E0E0),
    surface              = Color(0xFF1E1E1E),
    onSurface            = Color(0xFFE0E0E0),
    surfaceVariant       = Color(0xFF2C2C2C),
    onSurfaceVariant     = Color(0xFFB0B0B0),

    error                = Color(0xFFCF6679),
    errorContainer       = Color(0xFF8B0000),
    onErrorContainer     = Color(0xFFFFDAD6),
)

@Composable
fun App() {
    MaterialTheme(colorScheme = LightColors) {
        AppNavigation()
    }
}