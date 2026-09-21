package com.hivirtus.floatingmenu.data

import android.content.Context

object AppState {
    const val PREFS_LAUNCHER = "launcher"
    const val PREFS_APP = "hivirtus_app"

    var selectedVersion: Int = 2
    var versionName: String = "standard"
    var gameServer: Int = 5
    var hideEsp: Boolean = false
    var newTouch: Boolean = false
    var gyroscope: Boolean = false
    var enableVisual: Boolean = false
    var debugMode: Boolean = false
    var cheatVersion: Int = 1

    fun load(context: Context) {
        val launcher = context.getSharedPreferences(PREFS_LAUNCHER, Context.MODE_PRIVATE)
        val app = context.getSharedPreferences(PREFS_APP, Context.MODE_PRIVATE)
        selectedVersion = launcher.getInt("selected_version", 2)
        versionName = launcher.getString("version_name", "standard") ?: "standard"
        gameServer = app.getInt("game_server", 5)
        hideEsp = app.getBoolean("hide_esp", false)
        newTouch = app.getBoolean("new_touch", false)
        gyroscope = app.getBoolean("gyroscope", false)
        enableVisual = app.getBoolean("enable_visual", false)
        debugMode = app.getBoolean("debug_mode", false)
        cheatVersion = app.getInt("cheat_version", 1)
    }

    fun saveLauncher(context: Context) {
        context.getSharedPreferences(PREFS_LAUNCHER, Context.MODE_PRIVATE).edit()
            .putInt("selected_version", selectedVersion)
            .putString("version_name", versionName)
            .apply()
    }

    fun saveApp(context: Context) {
        context.getSharedPreferences(PREFS_APP, Context.MODE_PRIVATE).edit()
            .putInt("game_server", gameServer)
            .putBoolean("hide_esp", hideEsp)
            .putBoolean("new_touch", newTouch)
            .putBoolean("gyroscope", gyroscope)
            .putBoolean("enable_visual", enableVisual)
            .putBoolean("debug_mode", debugMode)
            .putInt("cheat_version", cheatVersion)
            .apply()
    }

    fun versionLabel(): String = when (selectedVersion) {
        1 -> "Lite"
        3 -> "Professional"
        else -> "Standard"
    }
}
