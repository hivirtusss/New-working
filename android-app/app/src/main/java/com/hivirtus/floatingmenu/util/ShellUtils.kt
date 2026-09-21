package com.hivirtus.floatingmenu.util

import java.io.BufferedReader
import java.io.DataOutputStream

object ShellUtils {
    fun runShell(command: String, asRoot: Boolean = true): Boolean {
        return try {
            val process = if (asRoot) {
                Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            } else {
                Runtime.getRuntime().exec(command)
            }
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    fun execute(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.trim()
        } catch (_: Exception) {
            ""
        }
    }

    fun isProcessRunning(name: String): Boolean {
        val output = execute("ps -A | grep $name")
        return output.contains(name)
    }

    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    fun hasDeviceRebooted(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "getprop net.need.reboot"))
            val line = BufferedReader(process.inputStream.reader()).readLine()
            process.waitFor()
            line != "true"
        } catch (_: Exception) {
            true
        }
    }

    fun setRebootProperty() {
        runShell("setprop net.need.reboot true")
    }
}
