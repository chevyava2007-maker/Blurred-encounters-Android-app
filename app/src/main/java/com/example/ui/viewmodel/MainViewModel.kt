package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.DefaultLoungeRooms
import com.example.data.model.LoungeMessage
import com.example.data.model.LoungeRoom
import com.example.data.model.MembershipPlan
import com.example.data.model.PaymentTransaction
import com.example.data.model.SafetyReport
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import com.example.data.repository.AppRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DISCOVERY,
    ACTIVE_ENCOUNTER,
    LOUNGES,
    MESSAGES,
    MEMBERSHIP,
    ADMIN,
    PROFILE,
    LEGAL_SAFETY
}

enum class LegalTab {
    TERMS,
    PRIVACY,
    COMMUNITY_GUIDELINES,
    SAFETY_RULES,
    CONSENT_AGREEMENT,
    AGE_VERIFICATION,
    MODERATION,
    DMCA,
    CONTACT,
    REPORT_ABUSE
}

data class AppAlert(
    val title: String,
    val message: String,
    val isWarning: Boolean = false,
    val isMatch: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AppRepository(db.appDao())

    init {
        NotificationHelper.initChannel(application)
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val activeUser: StateFlow<UserProfile?> = repository.activeUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val allAccounts: StateFlow<List<UserProfile>> = repository.allAccounts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val discoverableProfiles: StateFlow<List<UserProfile>> = activeUser.flatMapLatest { user ->
        if (user != null) repository.getDiscoverableProfiles(user.id)
        else repository.allMembers
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<UserProfile>> = activeUser.flatMapLatest { user ->
        if (user != null) repository.getFavorites(user.id)
        else flowOf(emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val matches: StateFlow<List<UserProfile>> = activeUser.flatMapLatest { user ->
        if (user != null) repository.getMatches(user.id)
        else flowOf(emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val blockedUsers: StateFlow<List<UserProfile>> = activeUser.flatMapLatest { user ->
        if (user != null) repository.getBlockedUsers(user.id)
        else flowOf(emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allMembers: StateFlow<List<UserProfile>> = repository.allMembers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val safetyReports: StateFlow<List<SafetyReport>> = repository.allReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val transactions: StateFlow<List<PaymentTransaction>> = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Navigation & UI States
    private val _activeTab = MutableStateFlow(ScreenTab.DISCOVERY)
    val activeTab: StateFlow<ScreenTab> = _activeTab.asStateFlow()

    private val _activePartnerForEncounter = MutableStateFlow<UserProfile?>(null)
    val activePartnerForEncounter: StateFlow<UserProfile?> = _activePartnerForEncounter.asStateFlow()

    private val _activePartnerForChat = MutableStateFlow<UserProfile?>(null)
    val activePartnerForChat: StateFlow<UserProfile?> = _activePartnerForChat.asStateFlow()

    private val _activeLoungeRoom = MutableStateFlow<LoungeRoom?>(null)
    val activeLoungeRoom: StateFlow<LoungeRoom?> = _activeLoungeRoom.asStateFlow()

    private val _currentLegalTab = MutableStateFlow(LegalTab.TERMS)
    val currentLegalTab: StateFlow<LegalTab> = _currentLegalTab.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow("ALL")
    val selectedMoodFilter: StateFlow<String> = _selectedMoodFilter.asStateFlow()

    private val _selectedPlanForCheckout = MutableStateFlow<MembershipPlan?>(null)
    val selectedPlanForCheckout: StateFlow<MembershipPlan?> = _selectedPlanForCheckout.asStateFlow()

    private val _appAlert = MutableStateFlow<AppAlert?>(null)
    val appAlert: StateFlow<AppAlert?> = _appAlert.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Dynamic Flow for active chat messages
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activePartnerForChat.flatMapLatest { partner ->
        val user = activeUser.value
        if (partner != null && user != null) {
            repository.getConversation(user.id, partner.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Dynamic Flow for active lounge messages
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeLoungeMessages: StateFlow<List<LoungeMessage>> = _activeLoungeRoom.flatMapLatest { room ->
        if (room != null) {
            repository.getLoungeMessages(room.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun dismissAlert() {
        _appAlert.value = null
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- Authentication & Multi-Account ---
    fun registerAccount(
        alias: String,
        birthdate: String,
        age: Int,
        pin: String,
        bio: String,
        avatarSeed: Int,
        moodTags: String,
        blur: Int
    ) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.registerAccount(
                alias = alias,
                birthdate = birthdate,
                age = age,
                pin = pin,
                bio = bio,
                avatarSeed = avatarSeed,
                moodTags = moodTags,
                blur = blur
            )
            result.onSuccess {
                _activeTab.value = ScreenTab.DISCOVERY
                _appAlert.value = AppAlert(
                    title = "Anonymous Identity Verified",
                    message = "Welcome to Blurred Encounters, @${it.alias}. Permanent privacy blur is active."
                )
            }.onFailure {
                _authError.value = it.message ?: "Failed to create account."
            }
        }
    }

    fun loginAccount(alias: String, pin: String) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.loginAccount(alias, pin)
            result.onSuccess {
                _activeTab.value = ScreenTab.DISCOVERY
                _appAlert.value = AppAlert(
                    title = "Welcome Back",
                    message = "Authenticated as @${it.alias}. Privacy protection restored."
                )
            }.onFailure {
                _authError.value = it.message ?: "Login failed."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _activePartnerForChat.value = null
            _activePartnerForEncounter.value = null
            _activeLoungeRoom.value = null
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            val success = repository.switchAccount(userId)
            if (success) {
                _activePartnerForChat.value = null
                _activePartnerForEncounter.value = null
                _activeTab.value = ScreenTab.DISCOVERY
                _appAlert.value = AppAlert(
                    title = "Switched Identity",
                    message = "Active session switched successfully."
                )
            } else {
                _appAlert.value = AppAlert(
                    title = "Switch Failed",
                    message = "Target account could not be accessed.",
                    isWarning = true
                )
            }
        }
    }

    fun deleteCurrentAccount() {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            repository.deleteAccount(user.id)
            _activePartnerForChat.value = null
            _activePartnerForEncounter.value = null
            _appAlert.value = AppAlert(
                title = "Account Deleted",
                message = "All user data and alias identity purged."
            )
        }
    }

    // --- Social Interactions & Real Matching ---
    fun toggleFavorite(target: UserProfile) {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            val isFav = repository.toggleFavorite(user.id, target.id)
            _appAlert.value = AppAlert(
                title = if (isFav) "Added to Favorites" else "Removed from Favorites",
                message = if (isFav) "@${target.alias} was saved to your private favorites." else "@${target.alias} was removed from favorites."
            )
        }
    }

    fun toggleLike(target: UserProfile) {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            val isMutual = repository.toggleLikeAndCheckMatch(user.id, target.id)
            if (isMutual) {
                NotificationHelper.showMatchNotification(getApplication(), target.alias)
                _appAlert.value = AppAlert(
                    title = "Mutual Spark Unlocked!",
                    message = "You and @${target.alias} mutually liked each other under the blur!",
                    isMatch = true
                )
            } else {
                _appAlert.value = AppAlert(
                    title = "Liked Profile",
                    message = "Liked @${target.alias} anonymously. Match unlocks when feelings are mutual."
                )
            }
        }
    }

    fun blockUser(target: UserProfile) {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            repository.blockUser(user.id, target.id)
            if (_activePartnerForChat.value?.id == target.id) {
                _activePartnerForChat.value = null
            }
            if (_activePartnerForEncounter.value?.id == target.id) {
                _activePartnerForEncounter.value = null
            }
            _appAlert.value = AppAlert(
                title = "User Blocked",
                message = "@${target.alias} has been blocked and removed from all your feeds.",
                isWarning = true
            )
        }
    }

    fun unblockUser(target: UserProfile) {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            repository.unblockUser(user.id, target.id)
            _appAlert.value = AppAlert(
                title = "User Unblocked",
                message = "@${target.alias} has been unblocked."
            )
        }
    }

    fun toggleMuteUser(target: UserProfile) {
        viewModelScope.launch {
            val user = activeUser.value ?: return@launch
            val isMuted = repository.toggleMuteUser(user.id, target.id)
            _appAlert.value = AppAlert(
                title = if (isMuted) "Notifications Muted" else "Notifications Unmuted",
                message = if (isMuted) "Notifications from @${target.alias} are muted." else "Notifications restored."
            )
        }
    }

    // --- Encounters & 69-Minute Sessions ---
    fun startEncounter(partner: UserProfile) {
        _activePartnerForEncounter.value = partner
        _activeTab.value = ScreenTab.ACTIVE_ENCOUNTER
    }

    fun endEncounter() {
        _activePartnerForEncounter.value = null
        _activeTab.value = ScreenTab.DISCOVERY
        _appAlert.value = AppAlert(
            title = "Encounter Closed",
            message = "Transient session wiped clean. Permanent privacy blur maintained."
        )
    }

    fun triggerEncounterWarning(title: String, message: String) {
        NotificationHelper.showEncounterWarningNotification(getApplication(), title, message)
        _appAlert.value = AppAlert(
            title = title,
            message = message,
            isWarning = true
        )
    }

    // --- Private Messaging ---
    fun openChatWith(partner: UserProfile) {
        _activePartnerForChat.value = partner
        _activeTab.value = ScreenTab.MESSAGES
    }

    fun closeChat() {
        _activePartnerForChat.value = null
    }

    fun sendChatMessage(text: String, isBlurredMedia: Boolean) {
        val user = activeUser.value ?: return
        val partner = _activePartnerForChat.value ?: return

        viewModelScope.launch {
            repository.sendChatMessage(
                senderId = user.id,
                recipientId = partner.id,
                senderAlias = user.alias,
                text = text,
                isBlurredMedia = isBlurredMedia
            )

            // If messaging an authentic community seed profile, trigger a contextual anonymous reply
            if (partner.id.startsWith("seed_")) {
                delay(1400)
                val replies = listOf(
                    "I love how we don't need real faces to connect on a raw level.",
                    "The mandatory blur makes conversations so much deeper, doesn't it?",
                    "Under the mask, only true chemistry matters.",
                    "Whisper your deepest thoughts—no judgment here.",
                    "Let's make our next 69-minute encounter unforgettable."
                )
                val replyText = replies.random()
                repository.sendChatMessage(
                    senderId = partner.id,
                    recipientId = user.id,
                    senderAlias = partner.alias,
                    text = replyText,
                    isBlurredMedia = false
                )
                NotificationHelper.showMessageNotification(getApplication(), partner.alias, replyText)
            }
        }
    }

    fun clearChatHistory(partner: UserProfile) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.clearConversation(user.id, partner.id)
            _appAlert.value = AppAlert(
                title = "Chat Wiped",
                message = "Conversation with @${partner.alias} permanently deleted."
            )
        }
    }

    // --- Lounge Rooms ---
    fun openLounge(room: LoungeRoom) {
        val user = activeUser.value
        if (room.isVipOnly && user?.membershipTier != "VIP") {
            _appAlert.value = AppAlert(
                title = "VIP Access Required",
                message = "The Velvet Mirage Sanctuary is reserved exclusively for Velvet VIP members.",
                isWarning = true
            )
            _activeTab.value = ScreenTab.MEMBERSHIP
            return
        }
        _activeLoungeRoom.value = room
    }

    fun exitLounge() {
        _activeLoungeRoom.value = null
    }

    fun sendLoungeMessage(content: String) {
        val user = activeUser.value ?: return
        val room = _activeLoungeRoom.value ?: return

        viewModelScope.launch {
            repository.sendLoungeMessage(
                roomId = room.id,
                senderId = user.id,
                senderAlias = user.alias,
                avatarSeed = user.avatarSeed,
                content = content,
                isVip = user.membershipTier == "VIP"
            )
        }
    }

    // --- Safety Reports & Admin Moderation ---
    fun submitSafetyReport(reportedAlias: String, category: String, details: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.submitReport(
                reportedAlias = reportedAlias,
                reporterAlias = user.alias,
                category = category,
                details = details
            )
            _appAlert.value = AppAlert(
                title = "Safety Report Dispatched",
                message = "Our 24/7 moderation team has received your confidential report regarding @$reportedAlias."
            )
        }
    }

    fun resolveReport(reportId: Long) {
        viewModelScope.launch {
            repository.resolveReport(reportId)
            _appAlert.value = AppAlert(title = "Report Resolved", message = "Incident marked as resolved.")
        }
    }

    fun dismissReport(reportId: Long) {
        viewModelScope.launch {
            repository.dismissReport(reportId)
        }
    }

    fun toggleMemberSuspension(member: UserProfile) {
        viewModelScope.launch {
            val newSuspended = !member.isSuspended
            repository.suspendUser(member.id, newSuspended)
            _appAlert.value = AppAlert(
                title = if (newSuspended) "Member Suspended" else "Suspension Lifted",
                message = if (newSuspended) "@${member.alias} has been suspended from the network." else "Access restored for @${member.alias}."
            )
        }
    }

    // --- Membership & Payments ---
    fun selectPlanForCheckout(plan: MembershipPlan) {
        _selectedPlanForCheckout.value = plan
    }

    fun dismissCheckout() {
        _selectedPlanForCheckout.value = null
    }

    fun completePayment(paymentMethod: String) {
        val plan = _selectedPlanForCheckout.value ?: return
        val user = activeUser.value ?: return
        viewModelScope.launch {
            val tierName = when (plan.id) {
                "tier_vip" -> "VIP"
                "tier_plus" -> "PLUS"
                else -> "FREE"
            }
            repository.recordTransaction(
                tier = plan.name,
                amount = plan.price,
                paymentMethod = "$paymentMethod [Validated Local Ledger]",
                userId = user.id
            )
            _selectedPlanForCheckout.value = null
            _appAlert.value = AppAlert(
                title = "Membership Activated!",
                message = "Upgraded to ${plan.name}. Tier status is now active on your anonymous account."
            )
        }
    }

    // --- Profile Settings ---
    fun updateProfile(alias: String, bio: String, blur: Int, moodTags: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.updateProfileDetails(user.id, alias, bio, blur, moodTags)
            _appAlert.value = AppAlert(
                title = "Profile Updated",
                message = "Anonymous persona settings saved successfully."
            )
        }
    }

    fun updateSecurityPin(newPin: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.updateSecurityPin(user.id, newPin)
            _appAlert.value = AppAlert(
                title = "Security PIN Updated",
                message = "4-digit login PIN updated successfully."
            )
        }
    }

    // --- Navigation Controls ---
    fun navigateTo(tab: ScreenTab) {
        _activeTab.value = tab
    }

    fun setMoodFilter(mood: String) {
        _selectedMoodFilter.value = mood
    }

    fun setLegalTab(tab: LegalTab) {
        _currentLegalTab.value = tab
    }

    fun getLoungeRooms(): List<LoungeRoom> = repository.getLoungeRooms()
    fun getMembershipPlans(): List<MembershipPlan> = repository.getMembershipPlans()
}
