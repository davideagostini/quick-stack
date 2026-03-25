package com.davideagostini.quickstack.feature.reminders

import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderScheduleCalculatorTest {
    @Test
    fun `one hour from now adds one hour`() {
        val now = Instant.parse("2026-03-24T10:15:00Z")

        val scheduled = ReminderScheduleCalculator.oneHourFromNow(now = now)

        assertEquals(Instant.parse("2026-03-24T11:15:00Z").toEpochMilli(), scheduled)
    }

    @Test
    fun `ten minutes from now adds ten minutes`() {
        val now = Instant.parse("2026-03-24T10:15:00Z")

        val scheduled = ReminderScheduleCalculator.tenMinutesFromNow(now = now)

        assertEquals(Instant.parse("2026-03-24T10:25:00Z").toEpochMilli(), scheduled)
    }

    @Test
    fun `tonight uses same day before 8pm`() {
        val now = Instant.parse("2026-03-24T17:00:00Z")
        val scheduled = ReminderScheduleCalculator.tonight(now = now, zoneId = ZoneId.of("UTC"))

        assertEquals(Instant.parse("2026-03-24T20:00:00Z").toEpochMilli(), scheduled)
    }

    @Test
    fun `tonight rolls to next day after 8pm`() {
        val now = Instant.parse("2026-03-24T21:00:00Z")
        val scheduled = ReminderScheduleCalculator.tonight(now = now, zoneId = ZoneId.of("UTC"))

        assertEquals(Instant.parse("2026-03-25T20:00:00Z").toEpochMilli(), scheduled)
    }
}
