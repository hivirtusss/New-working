package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.core.KernelBridge
import com.hivirtus.floatingmenu.core.StartGameController
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.data.GamePackages
import com.hivirtus.floatingmenu.databinding.ActivityDashboardBinding
import com.hivirtus.floatingmenu.overlay.FloatingMenuService
import com.hivirtus.floatingmenu.util.FileHelper
import com.hivirtus.floatingmenu.util.PermissionHelper
import java.io.File

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var startGame: StartGameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppState.load(this)
        startGame = StartGameController(this)
        PermissionHelper.ensureReady(this)

        bindDeviceInfo()
        updateVisualLabel()

        binding.startGameCard.setOnClickListener {
            if (binding.startGameText.text == getString(R.string.exit_app)) {
                finishAffinity()
                return@setOnClickListener
            }
            startGame.start()
        }

        binding.settingsRow.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.visualRow.setOnClickListener {
            AppState.enableVisual = !AppState.enableVisual
            AppState.saveApp(this)
            updateVisualLabel()
            Toast.makeText(
                this,
                if (AppState.enableVisual) R.string.visual_enabled else R.string.visual_disabled,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.driverRow.setOnClickListener {
            val msg = KernelBridge.loadKernelText(this)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.stopOverlay.setOnClickListener {
            stopService(Intent(this, FloatingMenuService::class.java))
            Toast.makeText(this, R.string.overlay_stopped, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        AppState.load(this)
        val game = GamePackages.byId(AppState.gameServer)
        binding.gameInfo.text = getString(R.string.selected_game, game?.label ?: "-")
        binding.coreInfo.text = if (FileHelper.hasCoreFile(filesDir)) {
            getString(R.string.core_ready)
        } else {
            getString(R.string.core_missing_short)
        }
        updateVisualLabel()
    }

    private fun bindDeviceInfo() {
        binding.phoneModel.text = Build.PRODUCT
        binding.androidVersion.text = "Android ${Build.VERSION.RELEASE}"
        binding.kernelVersion.text = System.getProperty("os.version") ?: "-"
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val w = metrics.widthPixels
        val h = metrics.heightPixels
        binding.screenSize.text = "${maxOf(w, h)} x ${minOf(w, h)}"
        binding.versionBadge.text = "v1.0.0-${AppState.versionName}-arm64"
    }

    private fun updateVisualLabel() {
        binding.visualText.text = getString(
            if (AppState.enableVisual) R.string.enable_visual_on else R.string.enable_visual_off
        )
        if (AppState.selectedVersion == 1) {
            binding.visualRow.visibility = android.view.View.GONE
        }
    }
}
