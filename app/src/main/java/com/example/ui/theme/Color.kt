package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val NeonMagenta = Color(0xFFFF2E93)
val NeonPurple = Color(0xFFA855F7)
val DeepViolet = Color(0xFF6366F1)
val NeonCyan = Color(0xFF00D2FF)
val ElectricBlue = Color(0xFF38BDF8)
val DarkBackground = Color(0xFF0A0713)
val DarkSurface = Color(0xFF130D23)
val DarkSurfaceVariant = Color(0xFF1E1436)
val DarkSurfaceHighlight = Color(0xFF2E1E52)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB3A7CB)
val TextMuted = Color(0xFF7A6D96)
val AccentGold = Color(0xFFFFC837)
val DangerRed = Color(0xFFFF3366)
val SafeGreen = Color(0xFF10B981)
val WarningOrange = Color(0xFFFF9900)
val BlurOverlayColor = Color(0xE6100922)

val BrandGradient = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonMagenta)
)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF6B21A8), Color(0xFFA855F7))
)

val GlowRadialGradient = Brush.radialGradient(
    colors = listOf(Color(0x33A855F7), Color(0x00000000))
)

