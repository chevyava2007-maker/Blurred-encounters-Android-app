package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.LoungeMessage
import com.example.data.model.PaymentTransaction
import com.example.data.model.SafetyReport
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        UserInteraction::class,
        ChatMessage::class,
        LoungeMessage::class,
        SafetyReport::class,
        PaymentTransaction::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "blurred_encounters.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
