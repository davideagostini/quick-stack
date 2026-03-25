package com.davideagostini.quickstack.di

import android.content.Context
import androidx.room.Room
import com.davideagostini.quickstack.data.local.QuickStackDatabase
import com.davideagostini.quickstack.data.local.dao.QuickItemDao
import com.davideagostini.quickstack.data.repository.ClipboardRepository
import com.davideagostini.quickstack.data.repository.QuickItemRepository
import com.davideagostini.quickstack.feature.notifications.QuickStackNotificationManager
import com.davideagostini.quickstack.feature.reminders.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Central Hilt module for the MVP.
 *
 * The app only needs a small set of singletons, so keeping them in one module
 * makes the dependency graph explicit and easy to follow.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Creates the single Room database instance used by the app.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuickStackDatabase {
        return Room.databaseBuilder(
            context,
            QuickStackDatabase::class.java,
            "quickstack.db",
        ).addMigrations(QuickStackDatabase.MIGRATION_1_2).build()
    }

    /**
     * Exposes the DAO separately so repositories can depend on the narrowest API.
     */
    @Provides
    fun provideQuickItemDao(database: QuickStackDatabase): QuickItemDao = database.quickItemDao()

    /**
     * Repository for inbox/history persistence and item state transitions.
     */
    @Provides
    @Singleton
    fun provideQuickItemRepository(quickItemDao: QuickItemDao): QuickItemRepository {
        return QuickItemRepository(quickItemDao)
    }

    /**
     * Clipboard access is app-wide and stateless, so a singleton is enough.
     */
    @Provides
    @Singleton
    fun provideClipboardRepository(@ApplicationContext context: Context): ClipboardRepository {
        return ClipboardRepository(context)
    }

    /**
     * Notification manager owns channel creation and pinned-notification updates.
     */
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): QuickStackNotificationManager {
        return QuickStackNotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): ReminderScheduler {
        return ReminderScheduler(context)
    }
}
