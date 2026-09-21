package com.hivirtus.floatingmenu.core

import android.app.Activity
import android.widget.Toast
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.data.GamePackages
import com.hivirtus.floatingmenu.util.FileHelper
import com.hivirtus.floatingmenu.util.ShellUtils
import java.util.concurrent.Executors

class StartGameController(private val activity: Activity) {
    private val executor = Executors.newSingleThreadExecutor()

    fun start(onFinished: () -> Unit = {}) {
        if (!FileHelper.hasCoreFile(activity.filesDir)) {
            Toast.makeText(activity, R.string.core_missing, Toast.LENGTH_LONG).show()
            return
        }

        executor.execute {
            val surfaceView = if (ShellUtils.hasDeviceRebooted()) 0 else 1
            val game = GamePackages.byId(AppState.gameServer)
            if (game == null) {
                activity.runOnUiThread {
                    Toast.makeText(activity, R.string.select_game_first, Toast.LENGTH_SHORT).show()
                }
                return@execute
            }

            if (ShellUtils.isProcessRunning("CF")) {
                activity.runOnUiThread {
                    Toast.makeText(activity, R.string.cheat_already_running, Toast.LENGTH_SHORT).show()
                }
                return@execute
            }

            ShellUtils.runShell("am start -n ${game.packageName}/${game.activity}")

            KernelBridge.saveMenuIni(
                surfaceView,
                AppState.hideEsp,
                AppState.gameServer,
                AppState.newTouch,
                AppState.enableVisual,
                AppState.gyroscope,
                AppState.selectedVersion
            )

            KernelBridge.getPid(
                activity,
                activity.filesDir.absolutePath,
                surfaceView,
                AppState.debugMode,
                AppState.hideEsp
            )

            ShellUtils.runShell("am force-stop ${activity.packageName}")

            activity.runOnUiThread {
                onFinished()
            }
        }
    }
}
