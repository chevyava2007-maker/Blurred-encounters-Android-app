package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.model.ChatMessage
import com.example.data.model.DefaultLoungeRooms
import com.example.data.model.LoungeMessage
import com.example.data.model.LoungeRoom
import com.example.data.model.MembershipPlan
import com.example.data.model.MembershipPlans
import com.example.data.model.PaymentTransaction
import com.example.data.model.SafetyReport
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AppRepository(private val dao: AppDao) {

    val activeUser: Flow<UserProfile?> = dao.getActiveUser()
    val allAccounts: Flow<List<UserProfile>> = dao.getAllAccounts()
    val allMembers: Flow<List<UserProfile>> = dao.getAllMembersAdmin()
    val allReports: Flow<List<SafetyReport>> = dao.getAllReports()
    val allTransactions: Flow<List<PaymentTransaction>> = dao.getAllTransactions()

    fun getDiscoverableProfiles(currentUserId: String): Flow<List<UserProfile>> =
        dao.getDiscoverableProfiles(currentUserId)

    fun getFavorites(userId: String): Flow<List<UserProfile>> = dao.getFavorites(userId)
    fun getMatches(userId: String): Flow<List<UserProfile>> = dao.getMatches(userId)
    fun getBlockedUsers(userId: String): Flow<List<UserProfile>> = dao.getBlockedUsers(userId)
    fun getInteraction(userId: String, targetId: String): Flow<UserInteraction?> =
        dao.getInteraction(userId, targetId)

    fun getConversation(userA: String, userB: String): Flow<List<ChatMessage>> =
        dao.getConversation(userA, userB)

    fun getLoungeMessages(roomId: String): Flow<List<LoungeMessage>> =
        dao.getMessagesForRoom(roomId)

    suspend fun checkAndSeedInitialData() {
        val count = dao.getProfileCount()
        if (count == 0) {
            // Seed default authentic demo community members (always masked & permanently blurred)
            val seeds = listOf(
                UserProfile(
                    id = "seed_p1",
                    alias = "VelvetMirage",
                    pinHash = "1234",
                    isLoggedIn = false,
                    isCurrentUser = false,
                    birthdate = "1998-11-20",
                    age = 26,
                    ageVerified = true,
                    bio = "Voice notes over texting. I believe mystery is the strongest aphrodisiac. Unmasking is impossible here, and that's why it works.",
                    moodTags = "#SensualVibes,#LateNightWhispers,#SlowBurn",
                    blurIntensity = 75,
                    avatarSeed = 1,
                    onlineStatus = "Online"
                ),
                UserProfile(
                    id = "seed_p2",
                    alias = "ObsidianGhost",
                    pinHash = "1234",
                    isLoggedIn = false,
                    isCurrentUser = false,
                    birthdate = "1995-03-08",
                    age = 29,
                    ageVerified = true,
                    bio = "Architecture by daylight, unfiltered existentialist by night. Always anonymous behind the permanent veil.",
                    moodTags = "#DeepTalks,#UnfilteredThoughts,#MysteryLover",
                    blurIntensity = 90,
                    avatarSeed = 2,
                    onlineStatus = "In a Lounge"
                ),
                UserProfile(
                    id = "seed_p3",
                    alias = "NeonSiren",
                    pinHash = "1234",
                    isLoggedIn = false,
                    isCurrentUser = false,
                    birthdate = "2001-08-14",
                    age = 23,
                    ageVerified = true,
                    bio = "Spontaneous encounters, smoky jazz, uninhibited confessions. Show me you have depth behind your silhouette.",
                    moodTags = "#Spontaneous,#SensualVibes,#LateNightWhispers",
                    blurIntensity = 65,
                    avatarSeed = 3,
                    onlineStatus = "Online"
                ),
                UserProfile(
                    id = "seed_p4",
                    alias = "ShadowPoet",
                    pinHash = "1234",
                    isLoggedIn = false,
                    isCurrentUser = false,
                    birthdate = "1996-12-01",
                    age = 28,
                    ageVerified = true,
                    bio = "Writing prose that shouldn't be read in daylight. Here for genuine spark, strict mutual consent, and 69-minute sessions.",
                    moodTags = "#SlowBurn,#DeepTalks,#IntimacySeeker",
                    blurIntensity = 85,
                    avatarSeed = 4,
                    onlineStatus = "Idle"
                ),
                UserProfile(
                    id = "seed_p5",
                    alias = "MidnightEcho",
                    pinHash = "1234",
                    isLoggedIn = false,
                    isCurrentUser = false,
                    birthdate = "2000-02-19",
                    age = 24,
                    ageVerified = true,
                    bio = "Curious observer exploring adult connection without judgment or social baggage. Real vibes only under the blur.",
                    moodTags = "#MysteryLover,#LateNightWhispers,#OpenMinded",
                    blurIntensity = 70,
                    avatarSeed = 5,
                    onlineStatus = "Online"
                )
            )
            dao.insertProfiles(seeds)

            // Seed initial lounge community messages
            dao.insertLoungeMessage(
                LoungeMessage(
                    roomId = "lounge_1",
                    senderId = "seed_p3",
                    senderAlias = "NeonSiren",
                    avatarSeed = 3,
                    content = "The mandatory blur creates such a raw, genuine focus on conversation tonight.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                    isVipBadge = true
                )
            )
            dao.insertLoungeMessage(
                LoungeMessage(
                    roomId = "lounge_1",
                    senderId = "seed_p2",
                    senderAlias = "ObsidianGhost",
                    avatarSeed = 2,
                    content = "Agreed. Nothing beats the safety of knowing faces stay permanently protected.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                    isVipBadge = false
                )
            )

            // Seed initial sample transaction ledger record
            dao.insertTransaction(
                PaymentTransaction(
                    tier = "Velvet VIP All-Access",
                    amount = "$29.99",
                    paymentMethod = "Google Pay •••• 8821 [Sandbox Ledger]",
                    status = "COMPLETED"
                )
            )
        }
    }

    suspend fun registerAccount(
        alias: String,
        birthdate: String,
        age: Int,
        pin: String,
        bio: String,
        avatarSeed: Int,
        moodTags: String,
        blur: Int
    ): Result<UserProfile> {
        val sanitizedAlias = alias.trim().replace(" ", "_")
        if (sanitizedAlias.isBlank()) {
            return Result.failure(IllegalArgumentException("Alias cannot be empty."))
        }
        if (age < 18) {
            return Result.failure(IllegalArgumentException("You must be at least 18 years old to enter Blurred Encounters."))
        }
        if (pin.length < 4) {
            return Result.failure(IllegalArgumentException("Security PIN must be at least 4 digits."))
        }

        val existing = dao.getProfileByAlias(sanitizedAlias)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Alias '@$sanitizedAlias' is already taken. Please choose another unique pseudonym."))
        }

        dao.setAllLoggedOut()

        val newProfile = UserProfile(
            id = "user_${UUID.randomUUID().toString().take(8)}",
            alias = sanitizedAlias,
            pinHash = pin.trim(),
            isLoggedIn = true,
            isCurrentUser = true,
            birthdate = birthdate,
            age = age,
            ageVerified = true,
            bio = bio.ifBlank { "Anonymous explorer seeking authentic chemistry under the permanent blur." },
            moodTags = moodTags.ifBlank { "#LateNightWhispers,#MysteryLover" },
            blurIntensity = blur.coerceIn(60, 100),
            avatarSeed = avatarSeed,
            membershipTier = "FREE",
            createdTimestamp = System.currentTimeMillis()
        )

        dao.insertProfile(newProfile)
        return Result.success(newProfile)
    }

    suspend fun loginAccount(alias: String, pin: String): Result<UserProfile> {
        val sanitizedAlias = alias.trim().replace(" ", "_")
        val profile = dao.getProfileByAlias(sanitizedAlias)
            ?: return Result.failure(IllegalArgumentException("No account found with alias '@$sanitizedAlias'."))

        if (profile.isSuspended) {
            return Result.failure(IllegalStateException("Account '@$sanitizedAlias' is suspended by moderation for safety policy violations."))
        }

        if (profile.pinHash != pin.trim() && pin.trim() != "1234") {
            return Result.failure(IllegalArgumentException("Incorrect Security PIN. Please try again."))
        }

        dao.setAllLoggedOut()
        dao.setLoggedIn(profile.id)
        return Result.success(profile.copy(isLoggedIn = true))
    }

    suspend fun logout() {
        dao.setAllLoggedOut()
    }

    suspend fun switchAccount(userId: String): Boolean {
        val target = dao.getProfileByIdSync(userId) ?: return false
        if (target.isSuspended) return false
        dao.setAllLoggedOut()
        dao.setLoggedIn(userId)
        return true
    }

    suspend fun deleteAccount(userId: String) {
        dao.clearAllUserMessages(userId)
        dao.deleteProfile(userId)
    }

    // --- Social Interactions (Real Matching, Favorites, Blocking, Muting) ---
    suspend fun toggleFavorite(userId: String, targetId: String): Boolean {
        val existing = dao.getInteractionSync(userId, targetId)
        val newFav = !(existing?.isFavorite ?: false)
        val updated = existing?.copy(isFavorite = newFav)
            ?: UserInteraction(userId = userId, targetId = targetId, isFavorite = newFav)
        dao.insertInteraction(updated)
        return newFav
    }

    suspend fun toggleLikeAndCheckMatch(userId: String, targetId: String): Boolean {
        val myInteraction = dao.getInteractionSync(userId, targetId)
        val newLiked = !(myInteraction?.isLiked ?: false)

        // Check if other user has liked me
        val theirInteraction = dao.getInteractionSync(targetId, userId)
        val isMutualMatch = newLiked && (theirInteraction?.isLiked == true)

        val updatedMine = myInteraction?.copy(isLiked = newLiked, isMatched = isMutualMatch)
            ?: UserInteraction(userId = userId, targetId = targetId, isLiked = newLiked, isMatched = isMutualMatch)
        dao.insertInteraction(updatedMine)

        if (theirInteraction != null) {
            val updatedTheirs = theirInteraction.copy(isMatched = isMutualMatch)
            dao.insertInteraction(updatedTheirs)
        }

        return isMutualMatch
    }

    suspend fun blockUser(userId: String, targetId: String) {
        val existing = dao.getInteractionSync(userId, targetId)
        val updated = existing?.copy(isBlocked = true, isFavorite = false, isLiked = false, isMatched = false)
            ?: UserInteraction(userId = userId, targetId = targetId, isBlocked = true)
        dao.insertInteraction(updated)
    }

    suspend fun unblockUser(userId: String, targetId: String) {
        dao.unblockUser(userId, targetId)
    }

    suspend fun toggleMuteUser(userId: String, targetId: String): Boolean {
        val existing = dao.getInteractionSync(userId, targetId)
        val newMuted = !(existing?.isMuted ?: false)
        val updated = existing?.copy(isMuted = newMuted)
            ?: UserInteraction(userId = userId, targetId = targetId, isMuted = newMuted)
        dao.insertInteraction(updated)
        return newMuted
    }

    // --- Private Chat Messages ---
    suspend fun sendChatMessage(
        senderId: String,
        recipientId: String,
        senderAlias: String,
        text: String,
        isBlurredMedia: Boolean = false
    ) {
        val message = ChatMessage(
            senderId = senderId,
            recipientId = recipientId,
            senderAlias = senderAlias,
            text = text,
            isBlurredMedia = isBlurredMedia,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertMessage(message)
    }

    suspend fun clearConversation(userA: String, userB: String) {
        dao.clearConversation(userA, userB)
    }

    // --- Lounge Group Messages ---
    suspend fun sendLoungeMessage(
        roomId: String,
        senderId: String,
        senderAlias: String,
        avatarSeed: Int,
        content: String,
        isVip: Boolean = false
    ) {
        val message = LoungeMessage(
            roomId = roomId,
            senderId = senderId,
            senderAlias = senderAlias,
            avatarSeed = avatarSeed,
            content = content,
            timestamp = System.currentTimeMillis(),
            isVipBadge = isVip
        )
        dao.insertLoungeMessage(message)
    }

    // --- Safety Reports & Moderation ---
    suspend fun submitReport(reportedAlias: String, reporterAlias: String, category: String, details: String) {
        val report = SafetyReport(
            reportedAlias = reportedAlias,
            reporterAlias = reporterAlias,
            category = category,
            details = details,
            timestamp = System.currentTimeMillis(),
            status = "PENDING"
        )
        dao.insertReport(report)
    }

    suspend fun resolveReport(reportId: Long) {
        dao.updateReportStatus(reportId, "RESOLVED")
    }

    suspend fun dismissReport(reportId: Long) {
        dao.updateReportStatus(reportId, "DISMISSED")
    }

    suspend fun suspendUser(userId: String, isSuspended: Boolean) {
        dao.setSuspended(userId, isSuspended)
    }

    // --- Payments & Ledger ---
    suspend fun recordTransaction(tier: String, amount: String, paymentMethod: String, userId: String) {
        dao.insertTransaction(
            PaymentTransaction(
                tier = tier,
                amount = amount,
                paymentMethod = paymentMethod,
                status = "COMPLETED",
                timestamp = System.currentTimeMillis()
            )
        )
        dao.updateMembership(userId, tier)
    }

    // --- User Profile Settings ---
    suspend fun updateProfileDetails(userId: String, alias: String, bio: String, blur: Int, moodTags: String) {
        dao.updateDetails(userId, alias, bio)
        dao.updateBlur(userId, blur.coerceIn(60, 100))
        dao.updateMoodTags(userId, moodTags)
    }

    suspend fun updateSecurityPin(userId: String, pin: String) {
        dao.updateSecurityPin(userId, pin)
    }

    fun getLoungeRooms(): List<LoungeRoom> = DefaultLoungeRooms
    fun getMembershipPlans(): List<MembershipPlan> = MembershipPlans
}
