package com.davideagostini.quickstack.feature.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.davideagostini.quickstack.domain.model.QuickItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(item: QuickItem): Boolean {
        val scheduledAt = item.scheduledAt ?: return false
        val manager = alarmManager ?: return false
        val pendingIntent = alarmPendingIntent(item.id)
        val triggerAt = maxOf(scheduledAt, System.currentTimeMillis() + 1_000L)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            manager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
        return true
    }

    fun cancel(itemId: Long) {
        val pendingIntent = alarmPendingIntent(itemId)
        alarmManager?.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun alarmPendingIntent(itemId: Long): PendingIntent {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_TRIGGER_SCHEDULED_ITEM
            putExtra(ReminderAlarmReceiver.EXTRA_ITEM_ID, itemId)
        }
        return PendingIntent.getBroadcast(
            context,
            itemId.requestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

private fun Long.requestCode(): Int = (this % Int.MAX_VALUE).toInt()
