package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.LoungeMessage
import com.example.data.model.PaymentTransaction
import com.example.data.model.SafetyReport
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Profiles & Authentication ---
    @Query("SELECT * FROM user_profiles WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE isCurrentUser = 1 LIMIT 1")
    fun getLegacyCurrentUser(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles ORDER BY createdTimestamp DESC")
    fun getAllAccounts(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id != :currentUserId AND isSuspended = 0 AND id NOT IN (SELECT targetId FROM user_interactions WHERE userId = :currentUserId AND isBlocked = 1)")
    fun getDiscoverableProfiles(currentUserId: String): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE isSuspended = 0")
    fun getAllMembers(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles")
    fun getAllMembersAdmin(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileByIdSync(id: String): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE LOWER(alias) = LOWER(:alias) LIMIT 1")
    suspend fun getProfileByAlias(alias: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfile>)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET isLoggedIn = 0")
    suspend fun setAllLoggedOut()

    @Query("UPDATE user_profiles SET isLoggedIn = 1 WHERE id = :id")
    suspend fun setLoggedIn(id: String)

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)

    @Query("UPDATE user_profiles SET isSuspended = :isSuspended WHERE id = :id")
    suspend fun setSuspended(id: String, isSuspended: Boolean)

    @Query("UPDATE user_profiles SET membershipTier = :tier WHERE id = :userId")
    suspend fun updateMembership(userId: String, tier: String)

    @Query("UPDATE user_profiles SET blurIntensity = :blur WHERE id = :userId")
    suspend fun updateBlur(userId: String, blur: Int)

    @Query("UPDATE user_profiles SET moodTags = :tags WHERE id = :userId")
    suspend fun updateMoodTags(userId: String, tags: String)

    @Query("UPDATE user_profiles SET alias = :alias, bio = :bio WHERE id = :userId")
    suspend fun updateDetails(userId: String, alias: String, bio: String)

    @Query("UPDATE user_profiles SET pinHash = :pinHash WHERE id = :userId")
    suspend fun updateSecurityPin(userId: String, pinHash: String)

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getProfileCount(): Int

    // --- User Interactions (Favorites, Matching, Blocking, Muting) ---
    @Query("SELECT * FROM user_interactions WHERE userId = :userId AND targetId = :targetId LIMIT 1")
    fun getInteraction(userId: String, targetId: String): Flow<UserInteraction?>

    @Query("SELECT * FROM user_interactions WHERE userId = :userId AND targetId = :targetId LIMIT 1")
    suspend fun getInteractionSync(userId: String, targetId: String): UserInteraction?

    @Query("SELECT * FROM user_interactions WHERE userId = :userId")
    fun getInteractionsForUser(userId: String): Flow<List<UserInteraction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: UserInteraction)

    @Query("SELECT u.* FROM user_profiles u INNER JOIN user_interactions i ON u.id = i.targetId WHERE i.userId = :userId AND i.isFavorite = 1 AND i.isBlocked = 0 AND u.isSuspended = 0")
    fun getFavorites(userId: String): Flow<List<UserProfile>>

    @Query("SELECT u.* FROM user_profiles u INNER JOIN user_interactions i ON u.id = i.targetId WHERE i.userId = :userId AND i.isMatched = 1 AND i.isBlocked = 0 AND u.isSuspended = 0")
    fun getMatches(userId: String): Flow<List<UserProfile>>

    @Query("SELECT u.* FROM user_profiles u INNER JOIN user_interactions i ON u.id = i.targetId WHERE i.userId = :userId AND i.isBlocked = 1")
    fun getBlockedUsers(userId: String): Flow<List<UserProfile>>

    @Query("UPDATE user_interactions SET isBlocked = 0 WHERE userId = :userId AND targetId = :targetId")
    suspend fun unblockUser(userId: String, targetId: String)

    // --- Chat Messages Between Separate User Accounts ---
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userA AND recipientId = :userB) OR (senderId = :userB AND recipientId = :userA) ORDER BY timestamp ASC")
    fun getConversation(userA: String, userB: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE (senderId = :userA AND recipientId = :userB) OR (senderId = :userB AND recipientId = :userA)")
    suspend fun clearConversation(userA: String, userB: String)

    @Query("DELETE FROM chat_messages WHERE senderId = :userId OR recipientId = :userId")
    suspend fun clearAllUserMessages(userId: String)

    // --- Lounge Messages ---
    @Query("SELECT * FROM lounge_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<LoungeMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoungeMessage(message: LoungeMessage)

    // --- Safety Reports ---
    @Query("SELECT * FROM safety_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<SafetyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SafetyReport)

    @Query("UPDATE safety_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    // --- Payment Transactions ---
    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: PaymentTransaction)
}
