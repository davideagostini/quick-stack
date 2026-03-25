package com.davideagostini.quickstack.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickItemDraftTest {
    @Test
    fun `buildTitle prefixes pinned clipboard items`() {
        val title = QuickItemDraft.buildTitle(
            type = QuickItemType.CLIPBOARD,
            content = "Copied text",
            isPinned = true,
        )

        assertEquals("Pinned clipboard: Copied text", title)
    }
}
