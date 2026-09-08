package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple

/**
 * Permanently blurred avatar component.
 * Unmasking is strictly disabled by design to enforce absolute adult user anonymity.
 */
@Composable
fun BlurredAvatar(
    seed: Int,
    blurIntensity: Int = 80,
    size: Dp = 80.dp,
    showRing: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val colorPalette = when (seed % 4) {
        0 -> listOf(Color(0xFF00D2FF), Color(0xFFFF2E93), Color(0xFF7928CA))
        1 -> listOf(Color(0xFFFF0080), Color(0xFF7928CA), Color(0xFF4F46E5))
        2 -> listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF3B82F6))
        else -> listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF8B5CF6))
    }

    Box(
        modifier = modifier
            .size(size)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing animated outer halo
        if (showRing) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = (this.size.minDimension / 2f) * pulse
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.35f
                    )
                )
            }
        }

        // Inner Avatar Box with Mandatory Gaussian Blur (minimum 60% blur enforced)
        val effectiveBlurDp = ((blurIntensity.coerceIn(60, 100)) / 5f).dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = colorPalette,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(listOf(NeonCyan, NeonMagenta, NeonPurple, NeonCyan)),
                    shape = CircleShape
                )
        ) {
            // Silhouette & Facial Haze Overlay (Always blurred)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(effectiveBlurDp)
            ) {
                // Head silhouette
                drawCircle(
                    color = Color(0x990A0713),
                    radius = this.size.minDimension * 0.32f,
                    center = Offset(this.size.width * 0.5f, this.size.height * 0.44f)
                )
                // Shoulders
                drawCircle(
                    color = Color(0xBB0A0713),
                    radius = this.size.minDimension * 0.48f,
                    center = Offset(this.size.width * 0.5f, this.size.height * 1.1f)
                )
                // Venetian Eye Mask Silhouette
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = this.size.minDimension * 0.09f,
                    center = Offset(this.size.width * 0.42f, this.size.height * 0.42f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = this.size.minDimension * 0.09f,
                    center = Offset(this.size.width * 0.58f, this.size.height * 0.42f)
                )
            }

            // Mandatory Privacy Lock Shield Glass Badge
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x400B0714)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(size * 0.34f)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Permanently Blurred & Protected",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(size * 0.2f)
                    )
                }
            }
        }
    }
}
