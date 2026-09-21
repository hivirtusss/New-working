package com.hivirtus.floatingmenu.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R

object PermissionHelper {
    const val OVERLAY_REQUEST = 4444

    fun ensureReady(activity: AppCompatActivity) {
        if (!RootUtils.isRooted()) {
            Toast.makeText(activity, R.string.root_fail, Toast.LENGTH_LONG).show()
        }
        if (!Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, R.string.overlay_fail, Toast.LENGTH_LONG).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, OVERLAY_REQUEST)
        }
    }

    fun hasOverlay(context: Context): Boolean = Settings.canDrawOverlays(context)
}
