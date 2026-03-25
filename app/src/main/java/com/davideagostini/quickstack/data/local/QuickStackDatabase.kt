package com.davideagostini.quickstack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.davideagostini.quickstack.data.local.dao.QuickItemDao
import com.davideagostini.quickstack.data.local.entity.QuickItemConverters
import com.davideagostini.quickstack.data.local.entity.QuickItemEntity

/**
 * Single local database for the MVP.
 *
 * It only stores quick items for now, which keeps migrations and wiring simple.
 */
@Database(
    entities = [QuickItemEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(QuickItemConverters::class)
abstract class QuickStackDatabase : RoomDatabase() {
    /**
     * DAO exposed to repositories. No UI layer should access Room directly.
     */
    abstract fun quickItemDao(): QuickItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE quick_items ADD COLUMN isTriggered INTEGER NOT NULL DEFAULT 0",
                )
            }
        }
    }
}
