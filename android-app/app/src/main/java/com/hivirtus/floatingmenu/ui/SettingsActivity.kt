package com.hivirtus.floatingmenu.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.GamePackages
import com.hivirtus.floatingmenu.data.MenuConfig
import com.hivirtus.floatingmenu.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var config: MenuConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        config = MenuConfig.load()
        bindToggles()
        bindGameButtons()
        updateGameLabel()

        binding.saveConfig.setOnClickListener {
            readToggles()
            config.save()
            Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindToggles() {
        binding.debugMode.isChecked = config.debugMode
        binding.hideEsp.isChecked = config.hideEsp
        binding.newTouch.isChecked = config.newTouch
        binding.gyroscope.isChecked = config.gyroscope
        binding.enableVisual.isChecked = config.enableVisual
    }

    private fun readToggles() {
        config.debugMode = binding.debugMode.isChecked
        config.hideEsp = binding.hideEsp.isChecked
        config.newTouch = binding.newTouch.isChecked
        config.gyroscope = binding.gyroscope.isChecked
        config.enableVisual = binding.enableVisual.isChecked
    }

    private fun bindGameButtons() {
        val buttons = mapOf(
            R.id.gameGlobal to 1,
            R.id.gameKorea to 2,
            R.id.gameRekoo to 3,
            R.id.gameVng to 4,
            R.id.gameIndia to 5
        )

        buttons.forEach { (viewId, serverId) ->
            findViewById<android.view.View>(viewId).setOnClickListener {
                config.gameServer = serverId
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
            R.id.gameGlobal to 1,
            R.id.gameKorea to 2,
            R.id.gameRekoo to 3,
            R.id.gameVng to 4,
            R.id.gameIndia to 5
        )
        map.forEach { (viewId, serverId) ->
            findViewById<android.view.View>(viewId).setBackgroundColor(
                if (config.gameServer == serverId) selected else normal
            )
        }
    }

    private fun updateGameLabel() {
        val game = GamePackages.byId(config.gameServer)
        binding.gameServer.text = getString(R.string.game_server_label, game?.packageName ?: "-")
    }
}
