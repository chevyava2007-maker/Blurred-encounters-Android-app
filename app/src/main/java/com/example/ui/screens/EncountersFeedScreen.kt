package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.BlurredAvatar
import com.example.ui.components.BrandHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EncountersFeedScreen(
    profiles: List<UserProfile>,
    favoritesList: List<UserProfile>,
    matchesList: List<UserProfile>,
    selectedMoodFilter: String,
    onSelectMoodFilter: (String) -> Unit,
    onStartEncounter: (UserProfile) -> Unit,
    onOpenChat: (UserProfile) -> Unit,
    onToggleFavorite: (UserProfile) -> Unit,
    onToggleLike: (UserProfile) -> Unit,
    onReportUser: (alias: String) -> Unit,
    onBlockUser: (UserProfile) -> Unit,
    onMuteUser: (UserProfile) -> Unit,
    onJoinBetaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = listOf(
        "ALL",
        "MATCHES",
        "FAVORITES",
        "#LateNightWhispers",
        "#MysteryLover",
        "#SensualVibes",
        "#DeepTalks",
        "#Spontaneous",
        "#SlowBurn",
        "#UnfilteredThoughts"
    )

    val favoriteIds = remember(favoritesList) { favoritesList.map { it.id }.toSet() }
    val matchIds = remember(matchesList) { matchesList.map { it.id }.toSet() }

    val filteredProfiles = remember(profiles, favoritesList, matchesList, selectedMoodFilter) {
        when (selectedMoodFilter) {
            "ALL" -> profiles
            "MATCHES" -> matchesList
            "FAVORITES" -> favoritesList
            else -> profiles.filter { it.moodTags.contains(selectedMoodFilter, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Brand Header with "Blurred faces. Real connections."
        item {
            BrandHeader(
                showHelpUsGrow = true,
                onJoinBetaClick = onJoinBetaClick
            )
        }

        // Mood & Category Filter Scroll
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "EXPLORE BY MOOD & CONNECTION",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moods.forEach { mood ->
                        val isSelected = selectedMoodFilter == mood
                        val isSpecialFilter = mood == "MATCHES" || mood == "FAVORITES"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        isSelected -> NeonMagenta
                                        isSpecialFilter -> DarkSurfaceVariant
                                        else -> DarkSurface
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkSurfaceHighlight,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { onSelectMoodFilter(mood) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = when (mood) {
                                    "MATCHES" -> "Mutual Matches (${matchesList.size})"
                                    "FAVORITES" -> "Favorites (${favoritesList.size})"
                                    else -> mood
                                },
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Feed Status Counter
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProfiles.size} Anonymous Personas Online",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Zero Unmasking Policy Active",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (filteredProfiles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Personas Found Under This Filter",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try switching to 'ALL' or browse another mood category.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onSelectMoodFilter("ALL") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("SHOW ALL PERSONAS", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // List of Anonymous Profiles
        items(filteredProfiles, key = { it.id }) { profile ->
            val isFavorite = favoriteIds.contains(profile.id)
            val isMatched = matchIds.contains(profile.id)

            EncounterProfileCard(
                profile = profile,
                isFavorite = isFavorite,
                isMatched = isMatched,
                onStartEncounter = { onStartEncounter(profile) },
                onOpenChat = { onOpenChat(profile) },
                onToggleFavorite = { onToggleFavorite(profile) },
                onToggleLike = { onToggleLike(profile) },
                onReportUser = { onReportUser(profile.alias) },
                onBlockUser = { onBlockUser(profile) },
                onMuteUser = { onMuteUser(profile) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EncounterProfileCard(
    profile: UserProfile,
    isFavorite: Boolean,
    isMatched: Boolean,
    onStartEncounter: () -> Unit,
    onOpenChat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleLike: () -> Unit,
    onReportUser: () -> Unit,
    onBlockUser: () -> Unit,
    onMuteUser: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, if (isMatched) NeonCyan else DarkSurfaceHighlight, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Permanently Blurred Avatar
            BlurredAvatar(
                seed = profile.avatarSeed,
                blurIntensity = profile.blurIntensity,
                size = 72.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "@${profile.alias}",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "• ${profile.age}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Online indicator
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SafeGreen)
                    )

                    if (isMatched) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("MATCH", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Permanent blur badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.blurIntensity}% Face Blur Shield",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${profile.onlineStatus}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                if (profile.membershipTier != "FREE") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentGold.copy(alpha = 0.2f))
                            .border(0.8.dp, AccentGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = profile.membershipTier,
                            color = AccentGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Options 3-dot dropdown menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Report Profile", color = DangerRed) },
                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed) },
                        onClick = {
                            showMenu = false
                            onReportUser()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Block & Hide", color = DangerRed) },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = DangerRed) },
                        onClick = {
                            showMenu = false
                            onBlockUser()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Mute Notifications", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            showMenu = false
                            onMuteUser()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Anonymous Bio
        Text(
            text = profile.bio,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mood Tags
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            profile.moodTags.split(",").forEach { tag ->
                if (tag.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(0.8.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag.trim(),
                            color = NeonMagenta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Favorite Toggle
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isFavorite) AccentGold.copy(alpha = 0.2f) else DarkSurface)
                    .border(1.dp, if (isFavorite) AccentGold else DarkSurfaceHighlight, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) AccentGold else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Like / Match Button
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isMatched) NeonCyan.copy(alpha = 0.2f) else DarkSurface)
                    .border(1.dp, if (isMatched) NeonCyan else DarkSurfaceHighlight, CircleShape)
            ) {
                Icon(
                    imageVector = if (isMatched) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like / Match",
                    tint = if (isMatched) NeonCyan else NeonMagenta,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Private Chat Button
            Button(
                onClick = onOpenChat,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WHISPER",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Start 69-Min Encounter Button
            Button(
                onClick = onStartEncounter,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandGradient)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "69-MIN ENCOUNTER",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
