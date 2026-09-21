package com.hivirtus.floatingmenu.util

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object RootUtils {
    fun isRooted(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su"
        )
        return paths.any { path -> java.io.File(path).exists() }
    }

    fun runShell(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    fun launchComponent(packageName: String, activity: String): Boolean {
        val cmd = "am start -n $packageName/$activity"
        return runShell(cmd)
    }
}
