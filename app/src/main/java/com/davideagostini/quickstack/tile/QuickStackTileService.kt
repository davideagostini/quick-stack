package com.davideagostini.quickstack.tile

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.feature.capture.QuickCaptureActivity
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.feature.capture.QuickCaptureActivity.Companion.extraSource

/**
 * Quick Settings Tile entry point.
 *
 * The tile does not perform capture inline; it collapses Quick Settings and delegates to the
 * lightweight capture Activity, which is the simplest stable Android-native surface here.
 */
class QuickStackTileService : TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        if (isLocked) {
            unlockAndRun { launchCapture() }
        } else {
            launchCapture()
        }
    }

    private fun launchCapture() {
        val intent = Intent(this, QuickCaptureActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(extraSource, QuickItemSource.TILE.name)
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateTile() {
        val tile = qsTile ?: return
        tile.label = getString(R.string.tile_label)
        tile.subtitle = getString(R.string.tile_subtitle)
        tile.stateDescription = getString(R.string.tile_subtitle)
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }
}
