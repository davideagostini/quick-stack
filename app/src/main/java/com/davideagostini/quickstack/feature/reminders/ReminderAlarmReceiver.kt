package com.davideagostini.quickstack.feature.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.davideagostini.quickstack.data.repository.QuickItemRepository
import com.davideagostini.quickstack.feature.notifications.QuickStackNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var quickItemRepository: QuickItemRepository
    @Inject lateinit var notificationManager: QuickStackNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TRIGGER_SCHEDULED_ITEM) return
        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L)
        if (itemId <= 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                quickItemRepository.markTriggered(itemId)
                val item = quickItemRepository.getItem(itemId) ?: return@launch
                if (!item.isCompleted) {
                    notificationManager.showTriggeredScheduledItem(item)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TRIGGER_SCHEDULED_ITEM = "com.davideagostini.quickstack.action.TRIGGER_SCHEDULED_ITEM"
        const val EXTRA_ITEM_ID = "extra_item_id"
    }
}
