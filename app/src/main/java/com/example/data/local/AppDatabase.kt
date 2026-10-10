package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AnnouncementDao
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.FineDao
import com.example.data.local.dao.GroupSettingsDao
import com.example.data.local.dao.LoanDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.PasswordResetDao
import com.example.data.local.dao.RepaymentDao
import com.example.data.local.dao.SavingsDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FineEntity
import com.example.data.local.entity.GroupSettingsEntity
import com.example.data.local.entity.LoanEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PasswordResetEntity
import com.example.data.local.entity.RepaymentEntity
import com.example.data.local.entity.SavingsEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SavingsEntity::class,
        FineEntity::class,
        LoanEntity::class,
        RepaymentEntity::class,
        TransactionEntity::class,
        NotificationEntity::class,
        AnnouncementEntity::class,
        ChatMessageEntity::class,
        PasswordResetEntity::class,
        GroupSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun savingsDao(): SavingsDao
    abstract fun fineDao(): FineDao
    abstract fun loanDao(): LoanDao
    abstract fun repaymentDao(): RepaymentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun passwordResetDao(): PasswordResetDao
    abstract fun groupSettingsDao(): GroupSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE announcements ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE announcements ADD COLUMN isPublished INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE announcements ADD COLUMN isVisibleToMembers INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE announcements ADD COLUMN startDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE announcements ADD COLUMN endDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE repayments ADD COLUMN paymentType TEXT NOT NULL DEFAULT 'PRINCIPAL'")
                db.execSQL("ALTER TABLE loans ADD COLUMN principalPaid REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE loans ADD COLUMN interestPaid REAL NOT NULL DEFAULT 0.0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ikibina_savings_group.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
