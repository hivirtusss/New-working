package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.databinding.ActivitySetupBinding
import com.hivirtus.floatingmenu.util.RootUtils

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkRoot.setOnClickListener { checkRoot() }
        binding.checkOverlay.setOnClickListener { requestOverlay() }
        binding.continueBtn.setOnClickListener { continueIfReady() }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun checkRoot() {
        if (RootUtils.isRooted()) {
            binding.rootStatus.text = getString(R.string.root_ok)
        } else {
            binding.rootStatus.text = getString(R.string.root_fail)
            Toast.makeText(this, R.string.root_fail, Toast.LENGTH_LONG).show()
        }
    }

    private fun requestOverlay() {
        if (Settings.canDrawOverlays(this)) {
            binding.overlayStatus.text = getString(R.string.overlay_ok)
            return
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun continueIfReady() {
        if (!RootUtils.isRooted()) {
            Toast.makeText(this, R.string.root_fail, Toast.LENGTH_SHORT).show()
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.overlay_fail, Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun updateStatus() {
        binding.rootStatus.text = if (RootUtils.isRooted()) {
            getString(R.string.root_ok)
        } else {
            getString(R.string.root_pending)
        }
        binding.overlayStatus.text = if (Settings.canDrawOverlays(this)) {
            getString(R.string.overlay_ok)
        } else {
            getString(R.string.overlay_pending)
        }
    }
}
