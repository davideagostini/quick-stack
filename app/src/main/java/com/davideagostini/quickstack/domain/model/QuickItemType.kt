package com.davideagostini.quickstack.domain.model

/**
 * High-level item categories. The MVP uses these to keep the schema stable while
 * leaving room for reminders and timers later.
 */
enum class QuickItemType {
    TEXT_NOTE,
    CLIPBOARD,
    REMINDER,
    TIMER,
}
