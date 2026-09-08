package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.BlurredAvatar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BrandGradient
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsScreen(
    user: UserProfile?,
    allAccounts: List<UserProfile>,
    blockedUsers: List<UserProfile>,
    onSaveProfile: (alias: String, bio: String, blur: Int, moodTags: String) -> Unit,
    onUpdatePin: (newPin: String) -> Unit,
    onSwitchAccount: (userId: String) -> Unit,
    onUnblockUser: (UserProfile) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToMembership: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize().background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Profile...", color = TextSecondary)
        }
        return
    }

    val allAvailableMoods = listOf(
        "#LateNightWhispers",
        "#MysteryLover",
        "#SensualVibes",
        "#DeepTalks",
        "#Spontaneous",
        "#SlowBurn",
        "#UnfilteredThoughts",
        "#IntimacySeeker",
        "#OpenMinded"
    )

    var aliasInput by remember(user.alias) { mutableStateOf(user.alias) }
    var bioInput by remember(user.bio) { mutableStateOf(user.bio) }
    var blurSlider by remember(user.blurIntensity) { mutableFloatStateOf(user.blurIntensity.toFloat().coerceIn(60f, 100f)) }
    var selectedMoods by remember(user.moodTags) {
        mutableStateOf(user.moodTags.split(",").filter { it.isNotBlank() }.toSet())
    }

    var newPinInput by remember { mutableStateOf("") }
    var showPinSavedToast by remember { mutableStateOf(false) }
    var showSaveToast by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "PROFILE & PRIVACY CONTROLS",
                    color = ElectricBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Anonymous Persona",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentGold.copy(alpha = 0.2f))
                    .border(1.dp, AccentGold, RoundedCornerShape(8.dp))
                    .clickable { onNavigateToMembership() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = user.membershipTier,
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar Preview (Always blurred)
        BlurredAvatar(
            seed = user.avatarSeed,
            blurIntensity = blurSlider.toInt(),
            size = 110.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enforced Privacy Blur: ${blurSlider.toInt()}%",
            color = NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Blur calibration card (Floor 60% to 100%)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mandatory Face Blur Shield",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Face blur remains active permanently across video calls and feeds. Unmasking is strictly disabled.",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Slider(
                value = blurSlider,
                onValueChange = { blurSlider = it },
                valueRange = 60f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = NeonMagenta,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = DarkSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alias & Bio Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Anonymous Alias (No Real Names)",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = aliasInput,
                onValueChange = { aliasInput = it.replace(" ", "_") },
                label = { Text("Pseudonym", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkSurfaceHighlight
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bioInput,
                onValueChange = { bioInput = it },
                label = { Text("Anonymous Desires & Bio", color = TextSecondary) },
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = DarkSurfaceHighlight
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood Tags Selection Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select Your Mood Tags",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                allAvailableMoods.forEach { mood ->
                    val isSelected = selectedMoods.contains(mood)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) NeonMagenta else DarkSurface)
                            .border(1.dp, if (isSelected) NeonCyan else DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                            .clickable {
                                selectedMoods = if (isSelected) selectedMoods - mood else selectedMoods + mood
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mood,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Profile Button
        Button(
            onClick = {
                onSaveProfile(
                    aliasInput.trim(),
                    bioInput.trim(),
                    blurSlider.toInt(),
                    selectedMoods.joinToString(",")
                )
                showSaveToast = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BrandGradient)
        ) {
            Text(
                text = if (showSaveToast) "CHANGES SAVED SUCCESSFULLY ✓" else "SAVE PROFILE CHANGES",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security PIN Update Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Security & Re-Entry PIN",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Used to authenticate when returning or switching back to this alias.",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) newPinInput = it },
                    label = { Text("New 4-digit PIN", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceHighlight
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newPinInput.length >= 4) {
                            onUpdatePin(newPinInput)
                            showPinSavedToast = true
                            newPinInput = ""
                        }
                    },
                    enabled = newPinInput.length >= 4,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        text = if (showPinSavedToast) "SAVED ✓" else "UPDATE PIN",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account Switcher Card (Real Persistent Multi-Account Testing & Switching)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Switch Account / Identity",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Switch between registered aliases on this device for multi-account testing.",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            allAccounts.forEach { acc ->
                val isCurrent = acc.id == user.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrent) NeonPurple.copy(alpha = 0.25f) else DarkSurface)
                        .border(1.dp, if (isCurrent) NeonCyan else DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                        .clickable { if (!isCurrent) onSwitchAccount(acc.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BlurredAvatar(seed = acc.avatarSeed, blurIntensity = 80, size = 32.dp, showRing = false)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "@${acc.alias}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${acc.membershipTier} • Age ${acc.age}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SafeGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "ACTIVE", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(text = "SWITCH", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Blocked Users Management
        if (blockedUsers.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Blocked Users (${blockedUsers.size})",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                blockedUsers.forEach { blocked ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BlurredAvatar(seed = blocked.avatarSeed, blurIntensity = 80, size = 28.dp, showRing = false)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "@${blocked.alias}", color = TextSecondary, fontSize = 13.sp)
                        }
                        Button(
                            onClick = { onUnblockUser(blocked) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("UNBLOCK", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Account Actions (Logout & Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LOG OUT", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { showDeleteConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .border(1.dp, DangerRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("DELETE ALIAS", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showDeleteConfirmDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DangerRed, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Delete Anonymous Alias?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This will permanently purge this pseudonym, private messages, and local data from the database. This action cannot be undone.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showDeleteConfirmDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CANCEL", color = TextSecondary, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                showDeleteConfirmDialog = false
                                onDeleteAccount()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("PURGE ALL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
