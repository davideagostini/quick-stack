package com.davideagostini.quickstack.data.repository

import com.davideagostini.quickstack.data.local.dao.QuickItemDao
import com.davideagostini.quickstack.data.local.entity.QuickItemEntity
import com.davideagostini.quickstack.data.local.entity.toDomain
import com.davideagostini.quickstack.domain.model.QuickItem
import com.davideagostini.quickstack.domain.model.QuickItemDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository boundary for quick items.
 *
 * It hides Room details from the rest of the app, owns entity/domain mapping, and keeps
 * ViewModels focused on state transitions instead of persistence mechanics.
 */
class QuickItemRepository @Inject constructor(
    private val quickItemDao: QuickItemDao,
) {
    /**
     * Streams the inbox/history as domain items sorted newest-first.
     */
    fun observeItems(): Flow<List<QuickItem>> = quickItemDao.observeAll().map { items ->
        items.map(QuickItemEntity::toDomain)
    }

    /**
     * Persists a new item and reloads the inserted row so callers receive the stored
     * representation, including generated identifiers and normalized values.
     */
    suspend fun createItem(draft: QuickItemDraft): QuickItem {
        val now = System.currentTimeMillis()
        val id = quickItemDao.insert(
            QuickItemEntity(
                type = draft.type,
                title = draft.title,
                content = draft.content,
                createdAt = now,
                scheduledAt = draft.scheduledAt,
                isPinned = draft.isPinned,
                isTriggered = false,
                isCompleted = false,
                source = draft.source,
            ),
        )
        return requireNotNull(quickItemDao.getById(id)) { "Inserted item missing for id=$id" }.toDomain()
    }

    /**
     * Hard delete used by the inbox/history screen.
     */
    suspend fun deleteItem(id: Long) {
        quickItemDao.deleteById(id)
    }

    /**
     * Transitions a pinned item back into a non-pinned history record.
     */
    suspend fun dismissPinned(id: Long) {
        quickItemDao.dismissPinned(id)
    }

    /**
     * Records completion while keeping the item in history.
     */
    suspend fun completePinned(id: Long) {
        quickItemDao.completePinned(id)
    }

    suspend fun markTriggered(id: Long) {
        quickItemDao.markTriggered(id)
    }

    suspend fun resolveTriggered(id: Long) {
        quickItemDao.resolveTriggered(id)
    }

    suspend fun getItem(id: Long): QuickItem? = quickItemDao.getById(id)?.toDomain()
}
