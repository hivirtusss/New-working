package com.hivirtus.floatingmenu.core

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.data.MenuConfig
import com.hivirtus.floatingmenu.overlay.FloatingMenuService
import com.hivirtus.floatingmenu.util.FileHelper
import com.hivirtus.floatingmenu.util.ShellUtils

/**
 * KC GL SuperJNI equivalent — app-side only.
 * Writes menu ini + starts overlay. No game memory hooks.
 */
object KernelBridge {
    private const val TAG = "KernelBridge"
    private const val INI_PATH = MenuConfig.CONFIG_PATH

    fun saveMenuIni(
        surfaceView: Int,
        hideEsp: Boolean,
        gameServer: Int,
        newTouch: Boolean,
        enableVisual: Boolean,
        gyroscope: Boolean,
        selectedVersion: Int
    ) {
        val ini = buildString {
            appendLine("# Hivirtus menu ini (KC GL compatible keys)")
            appendLine("surfaceview=$surfaceView")
            appendLine("hide_esp=${if (hideEsp) 1 else 0}")
            appendLine("game_server=$gameServer")
            appendLine("new_touch=${if (newTouch) 1 else 0}")
            appendLine("enable_visual=${if (enableVisual) 1 else 0}")
            appendLine("gyroscope=${if (gyroscope) 1 else 0}")
            appendLine("selected_version=$selectedVersion")
            appendLine("esp_box=${if (AppState.enableVisual) 1 else 0}")
            appendLine("esp_line=0")
            appendLine("esp_distance=0")
            appendLine("aim_assist=0")
            appendLine("recoil_control=0")
            appendLine("menu_open=1")
            appendLine("menu_x=100")
            appendLine("menu_y=200")
        }
        FileHelper.writeRootFile(ini, INI_PATH)
        Log.i(TAG, "SaveMenuIni done -> $INI_PATH")
    }

    fun getPid(
        context: Context,
        filesDir: String,
        surfaceView: Int,
        debug: Boolean,
        hideEsp: Boolean
    ): Int {
        Log.i(TAG, "getPID filesDir=$filesDir surface=$surfaceView debug=$debug hideEsp=$hideEsp")
        ContextCompat.startForegroundService(context, Intent(context, FloatingMenuService::class.java))
        return 1
    }

    fun loadKernelText(context: Context): String {
        val core = FileHelper.coreFile(context.filesDir)
        return if (core.exists()) "Kernel ready: ${core.name}" else "Core file missing"
    }

    fun loadingGyroscope(enabled: Boolean): String {
        return if (enabled) "Gyroscope loader started" else "Gyroscope disabled"
    }
}
