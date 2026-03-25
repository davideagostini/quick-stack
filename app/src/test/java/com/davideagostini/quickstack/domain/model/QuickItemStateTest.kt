package com.davideagostini.quickstack.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickItemStateTest {
    @Test
    fun `scheduled reminder is pending before trigger`() {
        val item = QuickItem(
            id = 1L,
            type = QuickItemType.REMINDER,
            title = "Reminder: Tonight",
            content = "Tonight",
            createdAt = 1L,
            scheduledAt = 2L,
            isPinned = false,
            isTriggered = false,
            isCompleted = false,
            source = QuickItemSource.APP,
        )

        assertTrue(item.isScheduled)
        assertTrue(item.isPendingSchedule)
        assertFalse(item.isTriggeredActionable)
    }

    @Test
    fun `triggered timer is actionable until completed`() {
        val item = QuickItem(
            id = 2L,
            type = QuickItemType.TIMER,
            title = "Timer: 10 minutes",
            content = "10 minutes",
            createdAt = 1L,
            scheduledAt = 2L,
            isPinned = false,
            isTriggered = true,
            isCompleted = false,
            source = QuickItemSource.TILE,
        )

        assertTrue(item.isScheduled)
        assertFalse(item.isPendingSchedule)
        assertTrue(item.isTriggeredActionable)
    }
}
