package com.davideagostini.quickstack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.davideagostini.quickstack.data.local.entity.QuickItemEntity

/**
 * Room DAO for the unified quick-item table.
 *
 * The contract stays intentionally small because the MVP only needs a chronological inbox
 * and a few state transitions for pinned items.
 */
@Dao
interface QuickItemDao {
    /**
     * Emits the full inbox ordered newest-first so the UI can render a simple history list.
     */
    @Query("SELECT * FROM quick_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<QuickItemEntity>>

    /**
     * Used when a repository needs to verify an inserted row or load a single item.
     */
    @Query("SELECT * FROM quick_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): QuickItemEntity?

    /**
     * Inserts a fully prepared entity and returns the generated primary key.
     */
    @Insert
    suspend fun insert(item: QuickItemEntity): Long

    /**
     * Hard delete for inbox cleanup.
     */
    @Query("DELETE FROM quick_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Marks a pinned notification as no longer active without deleting the history row.
     */
    @Query("UPDATE quick_items SET isPinned = 0 WHERE id = :id")
    suspend fun dismissPinned(id: Long)

    /**
     * Marks a pinned notification as completed and clears the pinned flag.
     */
    @Query("UPDATE quick_items SET isPinned = 0, isCompleted = 1 WHERE id = :id")
    suspend fun completePinned(id: Long)

    @Query("UPDATE quick_items SET isTriggered = 1 WHERE id = :id")
    suspend fun markTriggered(id: Long)

    @Query("UPDATE quick_items SET isTriggered = 0, isCompleted = 1 WHERE id = :id")
    suspend fun resolveTriggered(id: Long)
}
