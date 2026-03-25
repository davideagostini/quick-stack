package com.davideagostini.quickstack.feature.reminders

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object ReminderScheduleCalculator {
    private val tonightTime: LocalTime = LocalTime.of(20, 0)

    fun oneHourFromNow(
        minutesFromNow: Int = 60,
        now: Instant = Instant.now(),
    ): Long = now.plus(minutesFromNow.toLong(), ChronoUnit.MINUTES).toEpochMilli()

    fun tenMinutesFromNow(
        minutesFromNow: Int = 10,
        now: Instant = Instant.now(),
    ): Long = now.plus(minutesFromNow.toLong(), ChronoUnit.MINUTES).toEpochMilli()

    fun tonight(
        hour: Int = tonightTime.hour,
        minute: Int = tonightTime.minute,
        now: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Long {
        val current = LocalDateTime.ofInstant(now, zoneId)
        val targetTime = LocalTime.of(hour, minute)
        val candidate = current.toLocalDate().atTime(targetTime)
        val scheduled = if (current.isBefore(candidate)) {
            candidate
        } else {
            current.toLocalDate().plusDays(1).atTime(tonightTime)
        }
        return scheduled.atZone(zoneId).toInstant().toEpochMilli()
    }
}
