package com.davideagostini.quickstack.feature.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.davideagostini.quickstack.data.repository.QuickItemRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles notification actions for pinned items.
 *
 * The receiver updates persistence first and then clears the notification so the inbox and
 * system shade stay in sync even when the app UI is not running.
 */
@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
    @Inject lateinit var quickItemRepository: QuickItemRepository
    @Inject lateinit var notificationManager: QuickStackNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L)
        if (itemId <= 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_DISMISS_PINNED -> quickItemRepository.dismissPinned(itemId)
                    ACTION_COMPLETE_PINNED -> quickItemRepository.completePinned(itemId)
                    ACTION_RESTORE_PINNED -> {
                        val item = quickItemRepository.getItem(itemId) ?: return@launch
                        if (item.isActivePinned) {
                            notificationManager.showPinnedItem(item)
                        }
                        return@launch
                    }
                    ACTION_DISMISS_TRIGGERED,
                    ACTION_COMPLETE_TRIGGERED,
                    -> quickItemRepository.resolveTriggered(itemId)
                    else -> return@launch
                }
                notificationManager.cancelItemNotification(itemId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_DISMISS_PINNED = "com.davideagostini.quickstack.action.DISMISS_PINNED"
        const val ACTION_COMPLETE_PINNED = "com.davideagostini.quickstack.action.COMPLETE_PINNED"
        const val ACTION_RESTORE_PINNED = "com.davideagostini.quickstack.action.RESTORE_PINNED"
        const val ACTION_DISMISS_TRIGGERED = "com.davideagostini.quickstack.action.DISMISS_TRIGGERED"
        const val ACTION_COMPLETE_TRIGGERED = "com.davideagostini.quickstack.action.COMPLETE_TRIGGERED"
        private const val EXTRA_ITEM_ID = "extra_item_id"

        fun pendingIntent(context: Context, itemId: Long, action: String): PendingIntent {
            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                this.action = action
                putExtra(EXTRA_ITEM_ID, itemId)
            }
            return PendingIntent.getBroadcast(
                context,
                (itemId.hashCode() * 31) + action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
