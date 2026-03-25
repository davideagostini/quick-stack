package com.davideagostini.quickstack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davideagostini.quickstack.domain.model.QuickItem
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.domain.model.QuickItemType

/**
 * Room representation of a quick item.
 *
 * The entity mirrors the domain model closely so mapping stays trivial and
 * migration-friendly.
 */
@Entity(tableName = "quick_items")
data class QuickItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: QuickItemType,
    val title: String,
    val content: String,
    val createdAt: Long,
    val scheduledAt: Long?,
    val isPinned: Boolean,
    val isTriggered: Boolean,
    val isCompleted: Boolean,
    val source: QuickItemSource,
)

/**
 * Converts persisted data back into the domain model used by the rest of the app.
 */
fun QuickItemEntity.toDomain(): QuickItem = QuickItem(
    id = id,
    type = type,
    title = title,
    content = content,
    createdAt = createdAt,
    scheduledAt = scheduledAt,
    isPinned = isPinned,
    isTriggered = isTriggered,
    isCompleted = isCompleted,
    source = source,
)
