package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import java.util.Calendar

@Composable
fun OnboardingScreen(
    existingAccounts: List<UserProfile>,
    errorMessage: String?,
    onRegister: (alias: String, birthdate: String, age: Int, pin: String, bio: String, avatarSeed: Int, moodTags: String, blur: Int) -> Unit,
    onLogin: (alias: String, pin: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRegisterTab by remember { mutableStateOf(existingAccounts.isEmpty()) }

    // Register Form State
    val sampleAliases = remember {
        listOf("MidnightMuse", "ObsidianGhost", "NocturnalEcho", "VelvetDrifter", "ShadowWhisper", "VesperMind", "SmokyAura")
    }
    var alias by remember { mutableStateOf(sampleAliases.random()) }
    var pin by remember { mutableStateOf("1234") }
    var birthYear by remember { mutableIntStateOf(1998) }
    var birthMonth by remember { mutableIntStateOf(6) }
    var birthDay by remember { mutableIntStateOf(15) }
    var bio by remember { mutableStateOf("Drawn to authentic late-night conversations and deep chemistry.") }
    var blurSlider by remember { mutableFloatStateOf(80f) }
    var avatarSeed by remember { mutableIntStateOf(1) }

    var isEighteenChecked by remember { mutableStateOf(false) }
    var isPermanentBlurChecked by remember { mutableStateOf(false) }
    var isPolicyChecked by remember { mutableStateOf(false) }
    var localValidationError by remember { mutableStateOf<String?>(null) }

    // Login Form State
    var loginAlias by remember { mutableStateOf(existingAccounts.firstOrNull()?.alias ?: "") }
    var loginPin by remember { mutableStateOf("") }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val calculatedAge = currentYear - birthYear

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Crescent moon / shield logo
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
                .border(2.dp, NeonPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NightsStay,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Blurred Encounters",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "18+ ADULT ANONYMOUS VERIFICATION GATE",
            color = ElectricBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message banner
        val displayedError = errorMessage ?: localValidationError
        if (displayedError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DangerRed.copy(alpha = 0.2f))
                    .border(1.dp, DangerRed, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = displayedError, color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tab Switcher: Register vs Login
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isRegisterTab) NeonMagenta else Color.Transparent)
                    .clickable { isRegisterTab = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "New Alias (18+)",
                    color = if (isRegisterTab) Color.White else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isRegisterTab) NeonCyan else Color.Transparent)
                    .clickable { isRegisterTab = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login with PIN",
                    color = if (!isRegisterTab) DarkBackground else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isRegisterTab) {
            // === REGISTER TAB ===

            // Mandatory Blur & Safety Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mandatory Face Blur Shield",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Faces remain blurred at all times. Mutual unmasking is permanently disabled to guarantee complete privacy and zero real-world exposure.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar & Blur Preview
            BlurredAvatar(
                seed = avatarSeed,
                blurIntensity = blurSlider.toInt(),
                size = 90.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Permanent Blur: ${blurSlider.toInt()}%",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { avatarSeed = (avatarSeed + 1) % 10 },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Shuffle Avatar", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alias Input Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(text = "Choose Your Anonymous Alias", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "No real names or identifying handles permitted.", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = alias,
                        onValueChange = { alias = it.replace(" ", "_") },
                        placeholder = { Text("e.g. VelvetDrifter", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = DarkSurfaceHighlight
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { alias = sampleAliases.random() + "_" + (10..99).random() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Random Alias", tint = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "4-Digit Security PIN (For Re-Entry)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) pin = it },
                    placeholder = { Text("4 digits (e.g. 1234)", color = TextMuted) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceHighlight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Age Verification Card
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
                    Text(text = "Strict 18+ Age Gate", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Calculated Age: $calculatedAge",
                        color = if (calculatedAge >= 18) SafeGreen else DangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Birth Year
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Year", color = TextMuted, fontSize = 11.sp)
                        OutlinedTextField(
                            value = birthYear.toString(),
                            onValueChange = { birthYear = it.toIntOrNull() ?: 1998 },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceHighlight
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Month
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Month (1-12)", color = TextMuted, fontSize = 11.sp)
                        OutlinedTextField(
                            value = birthMonth.toString(),
                            onValueChange = { birthMonth = (it.toIntOrNull() ?: 1).coerceIn(1, 12) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceHighlight
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Day
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Day (1-31)", color = TextMuted, fontSize = 11.sp)
                        OutlinedTextField(
                            value = birthDay.toString(),
                            onValueChange = { birthDay = (it.toIntOrNull() ?: 1).coerceIn(1, 31) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceHighlight
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Privacy Blur Slider Card (Floor 60% - 100%)
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
                    Text(text = "Default Face Blur Density", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${blurSlider.toInt()}% (Locked)", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Controls the Gaussian frosted blur on your video and avatar (min 60% strictly enforced).",
                    color = TextMuted,
                    fontSize = 11.sp
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

            Spacer(modifier = Modifier.height(14.dp))

            // Mandatory Legal Checkboxes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isEighteenChecked,
                        onCheckedChange = { isEighteenChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = SafeGreen, uncheckedColor = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I certify under penalty of perjury that I am at least 18 years of age.",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPermanentBlurChecked,
                        onCheckedChange = { isPermanentBlurChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = NeonCyan, uncheckedColor = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I acknowledge that face blur is mandatory & unmasking is permanently disabled.",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPolicyChecked,
                        onCheckedChange = { isPolicyChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = NeonMagenta, uncheckedColor = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I agree to Alias-Only anonymity, Safety Rules, and Consent Agreement.",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Register Submit Button
            Button(
                onClick = {
                    localValidationError = null
                    if (calculatedAge < 18) {
                        localValidationError = "You must be 18 or older to enter Blurred Encounters."
                        return@Button
                    }
                    if (alias.trim().isBlank()) {
                        localValidationError = "Please choose an anonymous alias."
                        return@Button
                    }
                    if (pin.trim().length < 4) {
                        localValidationError = "Security PIN must be at least 4 digits."
                        return@Button
                    }
                    if (!isEighteenChecked || !isPermanentBlurChecked || !isPolicyChecked) {
                        localValidationError = "Please acknowledge all 3 mandatory compliance checkboxes."
                        return@Button
                    }

                    val birthdateStr = String.format("%04d-%02d-%02d", birthYear, birthMonth, birthDay)
                    onRegister(
                        alias.trim(),
                        birthdateStr,
                        calculatedAge,
                        pin.trim(),
                        bio.trim(),
                        avatarSeed,
                        "#LateNightWhispers,#MysteryLover",
                        blurSlider.toInt()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(BrandGradient)
            ) {
                Text(
                    text = "ENTER ANONYMOUSLY (18+ VERIFIED)",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }
        } else {
            // === LOGIN TAB ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Return to Your Alias",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select an existing profile or enter your pseudonym and security PIN.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Account Switcher Chips if accounts exist
                if (existingAccounts.isNotEmpty()) {
                    Text(text = "Accounts on this device:", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        existingAccounts.take(3).forEach { acc ->
                            val isSelected = loginAlias.equals(acc.alias, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonMagenta else DarkSurface)
                                    .border(1.dp, if (isSelected) NeonCyan else DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                                    .clickable { loginAlias = acc.alias }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BlurredAvatar(seed = acc.avatarSeed, blurIntensity = 80, size = 20.dp, showRing = false)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "@${acc.alias}",
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                OutlinedTextField(
                    value = loginAlias,
                    onValueChange = { loginAlias = it.replace(" ", "_") },
                    label = { Text("Pseudonym / Alias", color = TextSecondary) },
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
                    value = loginPin,
                    onValueChange = { if (it.length <= 6) loginPin = it },
                    label = { Text("Security PIN", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = DarkSurfaceHighlight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        localValidationError = null
                        if (loginAlias.trim().isBlank()) {
                            localValidationError = "Please enter your alias."
                            return@Button
                        }
                        if (loginPin.trim().isBlank()) {
                            localValidationError = "Please enter your 4-digit security PIN."
                            return@Button
                        }
                        onLogin(loginAlias.trim(), loginPin.trim())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = "AUTHENTICATE & ENTER",
                        color = DarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
