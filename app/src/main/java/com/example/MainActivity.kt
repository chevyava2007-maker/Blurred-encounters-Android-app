package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.ui.components.BlurredAvatar
import com.example.ui.components.SafetyReportDialog
import com.example.ui.screens.ActiveEncounterScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.EncountersFeedScreen
import com.example.ui.screens.LegalSafetyScreen
import com.example.ui.screens.LoungeScreen
import com.example.ui.screens.MembershipScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BlurredEncountersTheme
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LegalTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlurredEncountersTheme {
                BlurredEncountersApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BlurredEncountersApp(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Observe App State
    val activeUser by viewModel.activeUser.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val authError by viewModel.authError.collectAsState()

    val currentTab by viewModel.activeTab.collectAsState()
    val currentLegalTab by viewModel.currentLegalTab.collectAsState()
    val selectedMoodFilter by viewModel.selectedMoodFilter.collectAsState()

    val discoverableProfiles by viewModel.discoverableProfiles.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    val allMembers by viewModel.allMembers.collectAsState()
    val allReports by viewModel.safetyReports.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()

    val activePartnerForEncounter by viewModel.activePartnerForEncounter.collectAsState()
    val activePartnerForChat by viewModel.activePartnerForChat.collectAsState()
    val chatMessages by viewModel.activeChatMessages.collectAsState()

    val activeLoungeRoom by viewModel.activeLoungeRoom.collectAsState()
    val loungeMessages by viewModel.activeLoungeMessages.collectAsState()

    val selectedPlanForCheckout by viewModel.selectedPlanForCheckout.collectAsState()
    val appAlert by viewModel.appAlert.collectAsState()

    var reportTargetAlias by remember { mutableStateOf<String?>(null) }

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // If no active user session, display 18+ Verification & Authentication Gate
    if (activeUser == null) {
        OnboardingScreen(
            existingAccounts = allAccounts,
            errorMessage = authError,
            onRegister = { alias, birthdate, age, pin, bio, seed, moods, blur ->
                viewModel.registerAccount(alias, birthdate, age, pin, bio, seed, moods, blur)
            },
            onLogin = { alias, pin ->
                viewModel.loginAccount(alias, pin)
            }
        )
        return
    }

    val user = activeUser!!

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            // App Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceHighlight)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateTo(ScreenTab.DISCOVERY) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(1.5.dp, NeonPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BE",
                            color = NeonMagenta,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "BLURRED ENCOUNTERS",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "18+ Anonymous Social • @${user.alias}",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Quick Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Legal / Safety Suite
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenTab.LEGAL_SAFETY) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Legal & Policies",
                            tint = if (currentTab == ScreenTab.LEGAL_SAFETY) NeonMagenta else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // VIP Membership
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenTab.MEMBERSHIP) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "VIP Membership",
                            tint = if (currentTab == ScreenTab.MEMBERSHIP) AccentGold else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Admin Dashboard with live pending reports badge
                    val pendingReportsCount = allReports.count { it.status == "PENDING" }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenTab.ADMIN) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingReportsCount > 0) {
                                    Badge(containerColor = DangerRed) {
                                        Text("$pendingReportsCount", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Dashboard",
                                tint = if (currentTab == ScreenTab.ADMIN) NeonCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Profile & Avatar (Enforced Face Blur Shield)
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenTab.PROFILE) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        BlurredAvatar(
                            seed = user.avatarSeed,
                            blurIntensity = user.blurIntensity,
                            size = 28.dp,
                            showRing = currentTab == ScreenTab.PROFILE
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(1.dp, DarkSurfaceHighlight)
            ) {
                // 1. Encounters Discovery
                NavigationBarItem(
                    selected = currentTab == ScreenTab.DISCOVERY,
                    onClick = { viewModel.navigateTo(ScreenTab.DISCOVERY) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Encounters") },
                    label = { Text("Encounters", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonMagenta,
                        selectedTextColor = NeonMagenta,
                        indicatorColor = DarkSurfaceVariant,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    )
                )

                // 2. Lounges
                NavigationBarItem(
                    selected = currentTab == ScreenTab.LOUNGES,
                    onClick = { viewModel.navigateTo(ScreenTab.LOUNGES) },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Lounges") },
                    label = { Text("Lounges", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = DarkSurfaceVariant,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    )
                )

                // 3. 69m Encounter
                NavigationBarItem(
                    selected = currentTab == ScreenTab.ACTIVE_ENCOUNTER,
                    onClick = {
                        if (activePartnerForEncounter != null) {
                            viewModel.navigateTo(ScreenTab.ACTIVE_ENCOUNTER)
                        } else if (discoverableProfiles.isNotEmpty()) {
                            viewModel.startEncounter(discoverableProfiles.first())
                        }
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activePartnerForEncounter != null) {
                                    Badge(containerColor = NeonMagenta) {
                                        Text("LIVE", color = Color.White, fontSize = 8.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = "69m Call")
                        }
                    },
                    label = { Text("69m Call", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonMagenta,
                        selectedTextColor = NeonMagenta,
                        indicatorColor = DarkSurfaceVariant,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    )
                )

                // 4. Messages
                NavigationBarItem(
                    selected = currentTab == ScreenTab.MESSAGES,
                    onClick = {
                        if (activePartnerForChat == null && discoverableProfiles.isNotEmpty()) {
                            viewModel.openChatWith(discoverableProfiles.first())
                        } else {
                            viewModel.navigateTo(ScreenTab.MESSAGES)
                        }
                    },
                    icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Messages") },
                    label = { Text("Messages", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPurple,
                        selectedTextColor = NeonPurple,
                        indicatorColor = DarkSurfaceVariant,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    )
                )

                // 5. VIP Membership
                NavigationBarItem(
                    selected = currentTab == ScreenTab.MEMBERSHIP,
                    onClick = { viewModel.navigateTo(ScreenTab.MEMBERSHIP) },
                    icon = { Icon(Icons.Default.Star, contentDescription = "VIP") },
                    label = { Text("VIP", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGold,
                        selectedTextColor = AccentGold,
                        indicatorColor = DarkSurfaceVariant,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
                when (tab) {
                    ScreenTab.DISCOVERY -> {
                        EncountersFeedScreen(
                            profiles = discoverableProfiles,
                            favoritesList = favorites,
                            matchesList = matches,
                            selectedMoodFilter = selectedMoodFilter,
                            onSelectMoodFilter = { viewModel.setMoodFilter(it) },
                            onStartEncounter = { partner -> viewModel.startEncounter(partner) },
                            onOpenChat = { partner -> viewModel.openChatWith(partner) },
                            onToggleFavorite = { profile -> viewModel.toggleFavorite(profile) },
                            onToggleLike = { profile -> viewModel.toggleLike(profile) },
                            onReportUser = { alias -> reportTargetAlias = alias },
                            onBlockUser = { profile -> viewModel.blockUser(profile) },
                            onMuteUser = { profile -> viewModel.toggleMuteUser(profile) },
                            onJoinBetaClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blurredencounters.github.io/Blurred-Encounters-/"))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    ScreenTab.ACTIVE_ENCOUNTER -> {
                        val partner = activePartnerForEncounter ?: discoverableProfiles.firstOrNull()
                        if (partner != null) {
                            ActiveEncounterScreen(
                                partner = partner,
                                onEndEncounter = { viewModel.endEncounter() },
                                onWarningTriggered = { title, message -> viewModel.triggerEncounterWarning(title, message) },
                                onReportUser = { reportTargetAlias = partner.alias },
                                onBlockUser = { profile -> viewModel.blockUser(profile) }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No active encounter. Select a member from Discovery.", color = TextSecondary)
                            }
                        }
                    }

                    ScreenTab.LOUNGES -> {
                        LoungeScreen(
                            rooms = viewModel.getLoungeRooms(),
                            activeRoom = activeLoungeRoom,
                            messages = loungeMessages,
                            onSelectRoom = { room -> viewModel.openLounge(room) },
                            onSendMessage = { content -> viewModel.sendLoungeMessage(content) },
                            onBackToList = { viewModel.exitLounge() }
                        )
                    }

                    ScreenTab.MESSAGES -> {
                        val partner = activePartnerForChat ?: discoverableProfiles.firstOrNull()
                        if (partner != null) {
                            ChatScreen(
                                currentUserId = user.id,
                                partner = partner,
                                messages = chatMessages,
                                onSendMessage = { text, isMedia -> viewModel.sendChatMessage(text, isMedia) },
                                onStartEncounter = { viewModel.startEncounter(partner) },
                                onBack = { viewModel.closeChat() },
                                onReportUser = { reportTargetAlias = partner.alias },
                                onBlockUser = { viewModel.blockUser(partner) },
                                onClearHistory = { viewModel.clearChatHistory(partner) }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No active chat. Start a conversation from Discovery.", color = TextSecondary)
                            }
                        }
                    }

                    ScreenTab.MEMBERSHIP -> {
                        MembershipScreen(
                            plans = viewModel.getMembershipPlans(),
                            transactions = allTransactions,
                            currentTier = user.membershipTier,
                            selectedPlanForCheckout = selectedPlanForCheckout,
                            onSelectPlan = { plan -> viewModel.selectPlanForCheckout(plan) },
                            onDismissCheckout = { viewModel.dismissCheckout() },
                            onCompletePayment = { method -> viewModel.completePayment(method) }
                        )
                    }

                    ScreenTab.ADMIN -> {
                        AdminDashboardScreen(
                            members = allMembers,
                            reports = allReports,
                            onResolveReport = { id -> viewModel.resolveReport(id) },
                            onToggleSuspend = { member -> viewModel.toggleMemberSuspension(member) }
                        )
                    }

                    ScreenTab.PROFILE -> {
                        ProfileSettingsScreen(
                            user = user,
                            allAccounts = allAccounts,
                            blockedUsers = blockedUsers,
                            onSaveProfile = { alias, bio, blur, moods ->
                                viewModel.updateProfile(alias, bio, blur, moods)
                            },
                            onUpdatePin = { newPin -> viewModel.updateSecurityPin(newPin) },
                            onSwitchAccount = { userId -> viewModel.switchAccount(userId) },
                            onUnblockUser = { profile -> viewModel.unblockUser(profile) },
                            onLogout = { viewModel.logout() },
                            onDeleteAccount = { viewModel.deleteCurrentAccount() },
                            onNavigateToMembership = { viewModel.navigateTo(ScreenTab.MEMBERSHIP) }
                        )
                    }

                    ScreenTab.LEGAL_SAFETY -> {
                        LegalSafetyScreen(
                            currentTab = currentLegalTab,
                            onSelectTab = { viewModel.setLegalTab(it) },
                            onSubmitAbuseReport = { cat, details ->
                                viewModel.submitSafetyReport("Abuse Form: $cat", cat, details)
                            }
                        )
                    }
                }
            }

            // Global Safety Report Dialog
            reportTargetAlias?.let { alias ->
                SafetyReportDialog(
                    targetAlias = alias,
                    onDismiss = { reportTargetAlias = null },
                    onSubmit = { category, details ->
                        viewModel.submitSafetyReport(alias, category, details)
                        reportTargetAlias = null
                    }
                )
            }

            // In-App Alert Toast / Notification Banner
            appAlert?.let { alert ->
                Dialog(onDismissRequest = { viewModel.dismissAlert() }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, if (alert.isWarning) DangerRed else NeonCyan, RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when {
                                    alert.isWarning -> Icons.Default.Warning
                                    alert.isMatch -> Icons.Default.Star
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = when {
                                    alert.isWarning -> DangerRed
                                    alert.isMatch -> AccentGold
                                    else -> NeonCyan
                                },
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = alert.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = alert.message,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.dismissAlert() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(BrandGradient)
                            ) {
                                Text("UNDERSTOOD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
