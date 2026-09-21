package com.hivirtus.floatingmenu.data

object GamePackages {
    data class GameOption(val id: Int, val label: String, val packageName: String, val activity: String)

    val options = listOf(
        GameOption(1, "Global", "com.tencent.ig", "com.epicgames.ue4.SplashActivity"),
        GameOption(2, "Korea", "com.pubg.krmobile", "com.epicgames.ue4.SplashActivity"),
        GameOption(3, "TW/Rekoo", "com.rekoo.pubgm", "com.epicgames.ue4.SplashActivity"),
        GameOption(4, "VNG", "com.vng.pubgmobile", "com.epicgames.ue4.SplashActivity"),
        GameOption(5, "India (BGMI)", "com.pubg.imobile", "com.epicgames.ue4.SplashActivity")
    )

    fun byId(id: Int): GameOption? = options.firstOrNull { it.id == id }
}
