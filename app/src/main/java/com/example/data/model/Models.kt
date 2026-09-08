package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    indices = [Index(value = ["alias"], unique = true)]
)
data class UserProfile(
    @PrimaryKey val id: String,
    val alias: String,
    val pinHash: String = "1234",
    val isLoggedIn: Boolean = false,
    val isCurrentUser: Boolean = false,
    val birthdate: String = "1999-01-01",
    val age: Int = 25,
    val ageVerified: Boolean = true,
    val bio: String = "",
    val moodTags: String = "#LateNightWhispers,#MysteryLover",
    val blurIntensity: Int = 80,
    val membershipTier: String = "FREE",
    val avatarSeed: Int = 1,
    val genderInterest: String = "Everyone",
    val onlineStatus: String = "Online",
    val isSuspended: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "user_interactions",
    indices = [Index(value = ["userId", "targetId"], unique = true)]
)
data class UserInteraction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val targetId: String,
    val isFavorite: Boolean = false,
    val isLiked: Boolean = false,
    val isMatched: Boolean = false,
    val isBlocked: Boolean = false,
    val isMuted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val recipientId: String,
    val senderAlias: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isBlurredMedia: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "lounge_messages")
data class LoungeMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: String,
    val senderId: String,
    val senderAlias: String,
    val avatarSeed: Int,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVipBadge: Boolean = false
)

@Entity(tableName = "safety_reports")
data class SafetyReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportedAlias: String,
    val reporterAlias: String,
    val category: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING"
)

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tier: String,
    val amount: String,
    val paymentMethod: String,
    val status: String = "COMPLETED",
    val timestamp: Long = System.currentTimeMillis()
)

data class LoungeRoom(
    val id: String,
    val name: String,
    val vibeTag: String,
    val description: String,
    val activeMembersCount: Int,
    val isVipOnly: Boolean,
    val gradientColors: List<Long>
)

data class MembershipPlan(
    val id: String,
    val name: String,
    val price: String,
    val billingCycle: String,
    val badgeColor: Long,
    val features: List<String>,
    val isPopular: Boolean = false
)

val MembershipPlans = listOf(
    MembershipPlan(
        id = "tier_free",
        name = "Masked Explorer",
        price = "Free",
        billingCycle = "Forever",
        badgeColor = 0xFF00D2FF,
        features = listOf(
            "Access to standard public lounges",
            "Mandatory privacy blur shield (Always active)",
            "Up to 3 69-minute encounters daily",
            "Zero real-name anonymity protection",
            "Emergency panic button & report tools"
        )
    ),
    MembershipPlan(
        id = "tier_plus",
        name = "Blurred Plus",
        price = "$14.99",
        billingCycle = "/ month",
        badgeColor = 0xFFFF2E93,
        features = listOf(
            "Unlimited 69-minute encounters",
            "Customizable privacy blur density (60-100%)",
            "Priority matching in mood discovery",
            "Encrypted disappearing blurred photo peeks",
            "Read receipts & typing whispers",
            "Extended encounter sessions (+15 mins)"
        ),
        isPopular = true
    ),
    MembershipPlan(
        id = "tier_vip",
        name = "Velvet VIP All-Access",
        price = "$29.99",
        billingCycle = "/ month",
        badgeColor = 0xFFFFD700,
        features = listOf(
            "Exclusive access to Velvet Mirage VIP Sanctuary",
            "Golden VIP alias glow & verified badge",
            "Incognito browsing (view without appearing in feed)",
            "Direct 24/7 concierge & priority moderation",
            "Unrestricted 1-on-1 private messaging",
            "Early access to newly opened lounges"
        )
    )
)

val DefaultLoungeRooms = listOf(
    LoungeRoom(
        id = "lounge_1",
        name = "After Dark Confessions",
        vibeTag = "#SensualVibes",
        description = "Share unfiltered thoughts, intimate desires, and secret stories anonymously under the velvet cloak of night.",
        activeMembersCount = 42,
        isVipOnly = false,
        gradientColors = listOf(0xFF2E124D, 0xFF6B21A8)
    ),
    LoungeRoom(
        id = "lounge_2",
        name = "Midnight Speakeasy",
        vibeTag = "#MysteryLover",
        description = "Late night banter, intellectual intrigue, and mysterious chemistry with fellow nocturnal wanderers.",
        activeMembersCount = 28,
        isVipOnly = false,
        gradientColors = listOf(0xFF0F172A, 0xFF1E1B4B)
    ),
    LoungeRoom(
        id = "lounge_3",
        name = "Introspective Minds",
        vibeTag = "#DeepTalks",
        description = "A quiet sanctuary for authentic conversations, vulnerability, philosophy, and real emotional connection.",
        activeMembersCount = 19,
        isVipOnly = false,
        gradientColors = listOf(0xFF064E3B, 0xFF047857)
    ),
    LoungeRoom(
        id = "lounge_4",
        name = "Velvet Mirage Sanctuary",
        vibeTag = "#VIPOnly",
        description = "Exclusive high-discretion lounge reserved for Velvet VIP members seeking refined, private, top-tier encounters.",
        activeMembersCount = 14,
        isVipOnly = true,
        gradientColors = listOf(0xFF831843, 0xFFBE185D)
    )
)
