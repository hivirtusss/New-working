package com.hivirtus.floatingmenu.util

object RootUtils {
    fun isRooted(): Boolean = ShellUtils.isRootAvailable()

    fun launchComponent(packageName: String, activity: String): Boolean {
        return ShellUtils.runShell("am start -n $packageName/$activity")
    }
}
