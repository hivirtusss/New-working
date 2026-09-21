package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppState.load(this)
        highlightSelected()

        binding.liteCard.setOnClickListener { selectVersion(1, "lite") }
        binding.standardCard.setOnClickListener { selectVersion(2, "standard") }
        binding.proCard.setOnClickListener { selectVersion(3, "professional") }
        binding.goApp.setOnClickListener {
            AppState.saveLauncher(this)
            startActivity(Intent(this, DeployActivity::class.java))
            finish()
        }
    }

    private fun selectVersion(id: Int, name: String) {
        AppState.selectedVersion = id
        AppState.versionName = name
        highlightSelected()
        binding.goApp.visibility = android.view.View.VISIBLE
    }

    private fun highlightSelected() {
        val selected = Color.parseColor("#CCCCCC")
        val normal = Color.parseColor("#FFFFFF")
        binding.liteCard.setBackgroundColor(if (AppState.selectedVersion == 1) selected else normal)
        binding.standardCard.setBackgroundColor(if (AppState.selectedVersion == 2) selected else normal)
        binding.proCard.setBackgroundColor(if (AppState.selectedVersion == 3) selected else normal)
        binding.goApp.visibility = android.view.View.VISIBLE
    }
}
