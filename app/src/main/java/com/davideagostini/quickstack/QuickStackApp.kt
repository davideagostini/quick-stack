package com.davideagostini.quickstack

import android.app.Application
import com.davideagostini.quickstack.feature.notifications.QuickStackNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point.
 *
 * Hilt is bootstrapped here and process-wide Android integrations are initialized once,
 * before any screen, tile, or receiver tries to use them.
 */
@HiltAndroidApp
class QuickStackApp : Application() {
    @Inject lateinit var notificationManager: QuickStackNotificationManager

    override fun onCreate() {
        super.onCreate()
        // Create notification channels as soon as the process starts so tile- and app-driven
        // captures can publish persistent notifications without waiting for a screen to open.
        notificationManager.ensureChannels()
    }
}
