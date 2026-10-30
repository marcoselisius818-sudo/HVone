package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TikTokColorScheme = darkColorScheme(
  primary = NeonPink,
  onPrimary = Color.White,
  primaryContainer = SurfaceVariantDark,
  onPrimaryContainer = NeonPink,
  secondary = NeonCyan,
  onSecondary = Color.Black,
  secondaryContainer = SurfaceVariantDark,
  onSecondaryContainer = NeonCyan,
  tertiary = ElectricGold,
  onTertiary = Color.Black,
  background = BackgroundDark,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextSecondary,
  outline = CardBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false, // Keep consistent bold neon TikTok look
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = TikTokColorScheme,
    typography = Typography,
    content = content
  )
}
