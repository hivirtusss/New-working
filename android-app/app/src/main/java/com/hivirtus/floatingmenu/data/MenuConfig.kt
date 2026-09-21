package com.hivirtus.floatingmenu.data

import java.io.File

data class MenuConfig(
    var menuX: Int = 100,
    var menuY: Int = 200,
    var menuOpen: Boolean = false,
    var gameServer: Int = 5,
    var hideEsp: Boolean = false,
    var newTouch: Boolean = false,
    var gyroscope: Boolean = false,
    var enableVisual: Boolean = false,
    var debugMode: Boolean = false,
    var espBox: Boolean = false,
    var espLine: Boolean = false,
    var espDistance: Boolean = false,
    var aimAssist: Boolean = false,
    var recoilControl: Boolean = false
) {
    companion object {
        const val CONFIG_PATH = "/data/local/tmp/recoil_data_kcgl.ini"

        fun load(): MenuConfig {
            val file = File(CONFIG_PATH)
            if (!file.exists()) return MenuConfig()

            val values = mutableMapOf<String, String>()
            file.readLines().forEach { line ->
                if (line.startsWith("#") || !line.contains("=")) return@forEach
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) values[parts[0].trim()] = parts[1].trim()
            }

            return MenuConfig(
                menuX = values["menu_x"]?.toIntOrNull() ?: 100,
                menuY = values["menu_y"]?.toIntOrNull() ?: 200,
                menuOpen = values["menu_open"] == "1",
                gameServer = values["game_server"]?.toIntOrNull() ?: 5,
                hideEsp = values["hide_esp"] == "1",
                newTouch = values["new_touch"] == "1",
                gyroscope = values["gyroscope"] == "1",
                enableVisual = values["enable_visual"] == "1",
                debugMode = values["debug_mode"] == "1",
                espBox = values["esp_box"] == "1",
                espLine = values["esp_line"] == "1",
                espDistance = values["esp_distance"] == "1",
                aimAssist = values["aim_assist"] == "1",
                recoilControl = values["recoil_control"] == "1"
            )
        }
    }

    fun save() {
        val content = buildString {
            appendLine("# Hivirtus floating menu config")
            appendLine("menu_x=$menuX")
            appendLine("menu_y=$menuY")
            appendLine("menu_open=${if (menuOpen) 1 else 0}")
            appendLine("game_server=$gameServer")
            appendLine("hide_esp=${if (hideEsp) 1 else 0}")
            appendLine("new_touch=${if (newTouch) 1 else 0}")
            appendLine("gyroscope=${if (gyroscope) 1 else 0}")
            appendLine("enable_visual=${if (enableVisual) 1 else 0}")
            appendLine("debug_mode=${if (debugMode) 1 else 0}")
            appendLine("esp_box=${if (espBox) 1 else 0}")
            appendLine("esp_line=${if (espLine) 1 else 0}")
            appendLine("esp_distance=${if (espDistance) 1 else 0}")
            appendLine("aim_assist=${if (aimAssist) 1 else 0}")
            appendLine("recoil_control=${if (recoilControl) 1 else 0}")
        }

        runCatching {
            File(CONFIG_PATH).writeText(content)
        }
    }
}
