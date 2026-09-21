package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.GamePackages
import com.hivirtus.floatingmenu.data.MenuConfig
import com.hivirtus.floatingmenu.databinding.ActivityHomeBinding
import com.hivirtus.floatingmenu.overlay.FloatingMenuService
import com.hivirtus.floatingmenu.util.PermissionHelper
import com.hivirtus.floatingmenu.util.RootUtils

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var config = MenuConfig.load()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionHelper.ensureReady(this)
        updateGameLabel()

        binding.startGame.setOnClickListener { startGameFlow() }
        binding.openSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.stopOverlay.setOnClickListener {
            stopService(Intent(this, FloatingMenuService::class.java))
            Toast.makeText(this, R.string.overlay_stopped, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        config = MenuConfig.load()
        updateGameLabel()
    }

    private fun updateGameLabel() {
        val game = GamePackages.byId(config.gameServer)
        binding.gameLabel.text = getString(R.string.selected_game, game?.label ?: "None")
    }

    private fun startGameFlow() {
        if (!PermissionHelper.hasOverlay(this)) {
            PermissionHelper.ensureReady(this)
            return
        }

        val game = GamePackages.byId(config.gameServer)
        if (game == null) {
            Toast.makeText(this, R.string.select_game_first, Toast.LENGTH_SHORT).show()
            return
        }

        config.save()
        ContextCompat.startForegroundService(this, Intent(this, FloatingMenuService::class.java))

        val launched = RootUtils.launchComponent(game.packageName, game.activity)
        if (!launched) {
            Toast.makeText(this, R.string.launch_failed, Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, R.string.game_launched_overlay_on, Toast.LENGTH_SHORT).show()
    }
}
