package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserProfile
import com.example.ui.components.BlurredAvatar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun ActiveEncounterScreen(
    partner: UserProfile,
    onEndEncounter: () -> Unit,
    onReportUser: (alias: String) -> Unit,
    onBlockUser: (UserProfile) -> Unit,
    onWarningTriggered: (title: String, message: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // 69 minutes = 4140 seconds
    var secondsRemaining by remember { mutableIntStateOf(69 * 60) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }

    // Privacy blur intensity: Strictly enforced minimum 60% up to 100%
    var localBlurSlider by remember { mutableFloatStateOf(80f) }

    // Warning states
    var showTenMinWarningBanner by remember { mutableStateOf(false) }
    var showOneMinWarningBanner by remember { mutableStateOf(false) }
    var hasWarnedTenMin by remember { mutableStateOf(false) }
    var hasWarnedOneMin by remember { mutableStateOf(false) }
    var isSessionCompletedDialogShown by remember { mutableStateOf(false) }

    // Real countdown timer
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1

            // 10-Minute Warning
            if (secondsRemaining == 600 && !hasWarnedTenMin) {
                hasWarnedTenMin = true
                showTenMinWarningBanner = true
                onWarningTriggered(
                    "10 Minutes Remaining",
                    "Your 69-minute encounter with ${partner.alias} is nearing conclusion."
                )
            }

            // 1-Minute Warning
            if (secondsRemaining == 60 && !hasWarnedOneMin) {
                hasWarnedOneMin = true
                showOneMinWarningBanner = true
                onWarningTriggered(
                    "Final 60 Seconds",
                    "Encounter terminates in 1 minute. Say your parting words under the blur."
                )
            }
        }

        if (secondsRemaining <= 0) {
            isSessionCompletedDialogShown = true
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val isUrgent = secondsRemaining <= 300 // Last 5 minutes

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- Full-Screen Partner Simulated Video Feed (Permanently Blurred) ---
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isVideoOff) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0D0817)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BlurredAvatar(
                            seed = partner.avatarSeed,
                            blurIntensity = 95,
                            size = 140.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Video Stream Paused", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                // Live Frosted Canvas Shader with Mandatory Privacy Blur (Never unmasked)
                val blurRadius = (localBlurSlider / 4f).dp
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(blurRadius)
                ) {
                    val w = size.width
                    val h = size.height

                    // Ambient silhouettes and lighting
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.5f),
                                Color(0xFF08050F)
                            ),
                            center = Offset(w * 0.5f, h * 0.4f),
                            radius = w * 0.9f
                        )
                    )

                    // Head silhouette
                    drawCircle(
                        color = Color(0xDD0D0817),
                        radius = w * 0.28f,
                        center = Offset(w * 0.5f, h * 0.42f)
                    )

                    // Shoulders silhouette
                    drawCircle(
                        color = Color(0xEE0A0612),
                        radius = w * 0.55f,
                        center = Offset(w * 0.5f, h * 0.95f)
                    )

                    // Mood aura highlights
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonMagenta.copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(w * 0.7f, h * 0.35f),
                            radius = w * 0.35f
                        )
                    )
                }
            }
        }

        // --- Mandatory Privacy Shield Watermark / Enforcement Banner ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xB3000000))
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MANDATORY PRIVACY BLUR (Always Active • Unmasking Disabled)",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // --- Top Bar: Session Timer, Partner Info, Report & Panic Controls ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xF00A0713), Color.Transparent)
                    )
                )
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Partner Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BlurredAvatar(
                        seed = partner.avatarSeed,
                        blurIntensity = partner.blurIntensity,
                        size = 46.dp,
                        showRing = false
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = partner.alias,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SafeGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Encounter Active",
                                color = SafeGreen,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // 69-Minute Encounter Countdown Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isUrgent) DangerRed.copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .border(
                            1.5.dp,
                            if (isUrgent) DangerRed else NeonMagenta,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedTime,
                            color = if (isUrgent) DangerRed else NeonMagenta,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "/ 69:00",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Report & Block Action
                IconButton(
                    onClick = { onReportUser(partner.alias) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Report Partner",
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Warning alerts animated banners
            AnimatedVisibility(
                visible = showTenMinWarningBanner,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGold.copy(alpha = 0.2f))
                        .border(1.dp, AccentGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "10 MINUTES REMAINING — Session will close automatically at 69m.",
                                color = AccentGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { showTenMinWarningBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", color = AccentGold, fontSize = 12.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showOneMinWarningBanner,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DangerRed.copy(alpha = 0.25f))
                        .border(1.5.dp, DangerRed, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FINAL 60 SECONDS — Encounter session ending soon.",
                                color = DangerRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        IconButton(
                            onClick = { showOneMinWarningBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", color = DangerRed, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- My Local Pip Preview (Permanently Blurred at Bottom Right) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 180.dp)
                .size(width = 105.dp, height = 145.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
        ) {
            // Local camera preview with mandatory privacy blur
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .blur((localBlurSlider / 5f).dp)
            ) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.4f), Color(0xFF0F172A)),
                        center = center,
                        radius = size.width * 0.8f
                    )
                )
                drawCircle(
                    color = Color(0xCC090514),
                    radius = size.width * 0.32f,
                    center = Offset(size.width * 0.5f, size.height * 0.45f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x2A000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You (${localBlurSlider.toInt()}%)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Flip camera button
            IconButton(
                onClick = { isFrontCamera = !isFrontCamera },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(DarkSurface.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = TextPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // --- Bottom Controls Tray ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xF80A0713), Color(0xFF0A0713))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Privacy Blur Density Slider (Enforced range: 60% to 100%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant.copy(alpha = 0.85f))
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Privacy Blur Shield Density",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${localBlurSlider.toInt()}% (Protected)",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = localBlurSlider,
                    onValueChange = { localBlurSlider = it },
                    valueRange = 60f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonMagenta,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Mic, Panic Call End, Cam)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Mute Toggle
                IconButton(
                    onClick = { isMicMuted = !isMicMuted },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isMicMuted) DangerRed.copy(alpha = 0.3f) else DarkSurfaceVariant)
                        .border(1.dp, if (isMicMuted) DangerRed else DarkSurfaceHighlight, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute Microphone",
                        tint = if (isMicMuted) DangerRed else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Panic Button / Call End
                Button(
                    onClick = onEndEncounter,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PANIC DISCONNECT",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Video Toggle
                IconButton(
                    onClick = { isVideoOff = !isVideoOff },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isVideoOff) DangerRed.copy(alpha = 0.3f) else DarkSurfaceVariant)
                        .border(1.dp, if (isVideoOff) DangerRed else DarkSurfaceHighlight, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Toggle Video",
                        tint = if (isVideoOff) DangerRed else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Encounter Expired Dialog
    if (isSessionCompletedDialogShown) {
        Dialog(onDismissRequest = onEndEncounter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, NeonCyan, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "69-Minute Session Complete",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your time with ${partner.alias} has concluded. All transient session data has been wiped clean in compliance with safety standards.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onEndEncounter,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("RETURN TO DISCOVERY", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
