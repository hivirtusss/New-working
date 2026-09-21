package com.hivirtus.floatingmenu.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.data.GamePackages
import com.hivirtus.floatingmenu.data.MenuConfig
import com.hivirtus.floatingmenu.databinding.ActivitySettingsBinding
import com.hivirtus.floatingmenu.util.FileHelper

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppState.load(this)
        bindToggles()
        bindGameButtons()
        updateGameLabel()

        binding.saveConfig.setOnClickListener {
            readToggles()
            AppState.saveApp(this)
            syncMenuConfig()
            Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show()
        }

        binding.uploadConfig.setOnClickListener {
            syncMenuConfig()
            Toast.makeText(this, R.string.config_uploaded, Toast.LENGTH_SHORT).show()
        }

        binding.loadConfig.setOnClickListener {
            val cfg = MenuConfig.load()
            AppState.gameServer = cfg.gameServer
            AppState.hideEsp = cfg.hideEsp
            AppState.newTouch = cfg.newTouch
            AppState.gyroscope = cfg.gyroscope
            AppState.enableVisual = cfg.enableVisual
            AppState.debugMode = cfg.debugMode
            bindToggles()
            highlightSelectedGame()
            updateGameLabel()
            Toast.makeText(this, R.string.config_loaded, Toast.LENGTH_SHORT).show()
        }

        binding.deleteConfig.setOnClickListener {
            FileHelper.deleteCoreFile(filesDir)
            Toast.makeText(this, R.string.core_deleted, Toast.LENGTH_SHORT).show()
        }
    }

    private fun syncMenuConfig() {
        val cfg = MenuConfig(
            gameServer = AppState.gameServer,
            hideEsp = AppState.hideEsp,
            newTouch = AppState.newTouch,
            gyroscope = AppState.gyroscope,
            enableVisual = AppState.enableVisual,
            debugMode = AppState.debugMode,
            toggleEsp = AppState.enableVisual
        )
        cfg.save()
        FileHelper.writeRootFile(cfg.toIniString(), MenuConfig.CONFIG_PATH)
    }

    private fun bindToggles() {
        binding.debugMode.isChecked = AppState.debugMode
        binding.hideEsp.isChecked = AppState.hideEsp
        binding.newTouch.isChecked = AppState.newTouch
        binding.gyroscope.isChecked = AppState.gyroscope
        binding.enableVisual.isChecked = AppState.enableVisual
    }

    private fun readToggles() {
        AppState.debugMode = binding.debugMode.isChecked
        AppState.hideEsp = binding.hideEsp.isChecked
        AppState.newTouch = binding.newTouch.isChecked
        AppState.gyroscope = binding.gyroscope.isChecked
        AppState.enableVisual = binding.enableVisual.isChecked
    }

    private fun bindGameButtons() {
        val buttons = mapOf(
            R.id.game_global to 1,
            R.id.game_korea to 2,
            R.id.game_rekoo to 3,
            R.id.game_vng to 4,
            R.id.game_india to 5
        )
        buttons.forEach { (viewId, serverId) ->
            findViewById<android.view.View>(viewId).setOnClickListener {
                AppState.gameServer = serverId
                highlightSelectedGame()
                updateGameLabel()
            }
        }
        highlightSelectedGame()
    }

    private fun highlightSelectedGame() {
        val selected = Color.parseColor("#888888")
        val normal = Color.parseColor("#EEEEEE")
        val map = mapOf(
            R.id.game_global to 1,
            R.id.game_korea to 2,
            R.id.game_rekoo to 3,
            R.id.game_vng to 4,
            R.id.game_india to 5
        )
        map.forEach { (viewId, serverId) ->
            findViewById<android.view.View>(viewId).setBackgroundColor(
                if (AppState.gameServer == serverId) selected else normal
            )
        }
    }

    private fun updateGameLabel() {
        val game = GamePackages.byId(AppState.gameServer)
        binding.gameServer.text = getString(R.string.game_server_label, game?.packageName ?: "-")
    }
}
