package com.davideagostini.quickstack.domain.model

/**
 * Canonical quick-action item used by UI and business logic.
 *
 * This model stays free of Room annotations so the rest of the app can work with a
 * stable domain shape instead of depending on persistence details.
 */
data class QuickItem(
    val id: Long,
    val type: QuickItemType,
    val title: String,
    val content: String,
    val createdAt: Long,
    val scheduledAt: Long?,
    val isPinned: Boolean,
    val isTriggered: Boolean,
    val isCompleted: Boolean,
    val source: QuickItemSource,
) {
    /**
     * A pinned item is only considered active until it is completed or dismissed.
     */
    val isActivePinned: Boolean
        get() = isPinned && !isCompleted

    val isScheduled: Boolean
        get() = type == QuickItemType.REMINDER || type == QuickItemType.TIMER

    val isPendingSchedule: Boolean
        get() = isScheduled && !isTriggered && !isCompleted

    val isTriggeredActionable: Boolean
        get() = isScheduled && isTriggered && !isCompleted
}
