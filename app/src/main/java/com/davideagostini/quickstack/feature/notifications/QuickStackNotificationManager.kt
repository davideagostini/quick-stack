package com.davideagostini.quickstack.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.davideagostini.quickstack.MainActivity
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.domain.model.QuickItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Publishes and clears pinned item notifications.
 *
 * This component owns the Android notification wiring so ViewModels can request pin/unpin
 * behavior without depending directly on framework APIs.
 */
class QuickStackNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val systemManager = NotificationManagerCompat.from(context)

    fun ensureChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_PINNED,
            context.getString(R.string.notification_channel_pinned_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_pinned_description)
        }
        manager.createNotificationChannel(channel)
        val scheduledChannel = NotificationChannel(
            CHANNEL_SCHEDULED,
            context.getString(R.string.notification_channel_scheduled_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_scheduled_description)
        }
        manager.createNotificationChannel(scheduledChannel)
    }

    fun showPinnedItem(item: QuickItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val openIntent = PendingIntent.getActivity(
            context,
            item.id.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val dismissIntent = NotificationActionReceiver.pendingIntent(
            context = context,
            itemId = item.id,
            action = NotificationActionReceiver.ACTION_DISMISS_PINNED,
        )
        val completeIntent = NotificationActionReceiver.pendingIntent(
            context = context,
            itemId = item.id,
            action = NotificationActionReceiver.ACTION_COMPLETE_PINNED,
        )
        val deleteIntent = NotificationActionReceiver.pendingIntent(
            context = context,
            itemId = item.id,
            action = NotificationActionReceiver.ACTION_RESTORE_PINNED,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PINNED)
            .setSmallIcon(R.drawable.ic_tile_quickstack)
            .setContentTitle(item.title)
            .setContentText(item.content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.content))
            .setOngoing(true)
            .setContentIntent(openIntent)
            .setDeleteIntent(deleteIntent)
            .setOnlyAlertOnce(true)
            .addAction(0, context.getString(R.string.action_dismiss), dismissIntent)
            .addAction(0, context.getString(R.string.action_complete), completeIntent)
            .build()

        systemManager.notify(item.id.notificationId(), notification)
    }

    fun showTriggeredScheduledItem(item: QuickItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val openIntent = PendingIntent.getActivity(
            context,
            item.id.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val dismissIntent = NotificationActionReceiver.pendingIntent(
            context = context,
            itemId = item.id,
            action = NotificationActionReceiver.ACTION_DISMISS_TRIGGERED,
        )
        val completeIntent = NotificationActionReceiver.pendingIntent(
            context = context,
            itemId = item.id,
            action = NotificationActionReceiver.ACTION_COMPLETE_TRIGGERED,
        )
        val category = when (item.type) {
            com.davideagostini.quickstack.domain.model.QuickItemType.TIMER -> NotificationCompat.CATEGORY_ALARM
            else -> NotificationCompat.CATEGORY_REMINDER
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_SCHEDULED)
            .setSmallIcon(R.drawable.ic_tile_quickstack)
            .setContentTitle(item.title)
            .setContentText(item.content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.content))
            .setCategory(category)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .addAction(0, context.getString(R.string.action_dismiss), dismissIntent)
            .addAction(0, context.getString(R.string.action_complete), completeIntent)
            .build()

        systemManager.notify(item.id.notificationId(), notification)
    }

    fun cancelItemNotification(itemId: Long) {
        systemManager.cancel(itemId.notificationId())
    }

    companion object {
        const val CHANNEL_PINNED = "pinned_items"
        const val CHANNEL_SCHEDULED = "scheduled_items"
    }
}

/** Keeps notification IDs stable per item so updates and cancellations target the same slot. */
fun Long.notificationId(): Int = (this % Int.MAX_VALUE).toInt()
