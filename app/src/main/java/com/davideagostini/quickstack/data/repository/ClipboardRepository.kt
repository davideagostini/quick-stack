package com.davideagostini.quickstack.data.repository

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Small wrapper around the Android clipboard service.
 *
 * Keeping clipboard access here makes the capture ViewModel easier to test and
 * keeps platform-specific handling out of the UI layer.
 */
class ClipboardRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val appContext = context.applicationContext
    private val clipboardManager = context.getSystemService(ClipboardManager::class.java)

    fun readLatestText(): String? {
        return runCatching {
            val clip = clipboardManager?.primaryClip ?: return null
            if (clip.itemCount <= 0) return null
            val item = clip.getItemAt(0) ?: return null
            val description = clipboardManager.primaryClipDescription
            // Ignore non-text clipboard payloads because the MVP only supports text capture.
            if (description != null &&
                !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
                !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
            ) {
                return null
            }
            item.coerceToText(appContext).toString().trim().takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}
