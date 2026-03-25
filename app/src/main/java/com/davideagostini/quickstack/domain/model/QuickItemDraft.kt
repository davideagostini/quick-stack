package com.davideagostini.quickstack.domain.model

/**
 * Input used by the capture flow before the item is persisted.
 *
 * The draft carries only the data needed to create a stored item; the repository
 * is responsible for assigning id and timestamps.
 */
data class QuickItemDraft(
    val type: QuickItemType,
    val content: String,
    val isPinned: Boolean,
    val scheduledAt: Long? = null,
    val source: QuickItemSource,
) {
    /**
     * Keeps item titles short and predictable so the inbox and pinned notification
     * both remain readable.
     */
    val title: String = buildTitle(type = type, content = content, isPinned = isPinned)

    companion object {
        /**
         * Derives a lightweight title from the first non-empty line of the content.
         */
        fun buildTitle(type: QuickItemType, content: String, isPinned: Boolean): String {
            val preview = content.lineSequence()
                .firstOrNull()
                .orEmpty()
                .trim()
                .take(40)
            val base = if (preview.isBlank()) "Untitled" else preview
            return when {
                type == QuickItemType.REMINDER -> "Reminder: $base"
                type == QuickItemType.TIMER -> "Timer: $base"
                isPinned && type == QuickItemType.CLIPBOARD -> "Pinned clipboard: $base"
                isPinned -> "Pinned note: $base"
                type == QuickItemType.CLIPBOARD -> "Clipboard: $base"
                else -> "Note: $base"
            }
        }
    }
}
