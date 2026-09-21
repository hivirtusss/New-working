package com.hivirtus.floatingmenu.core

import android.content.Context
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.util.FileHelper
import java.io.File

object AssetDeployer {
    private val imageSets = listOf(
        Pair(11, "Menu")
    )

    fun deployAll(context: Context) {
        clearCache(context)
        extractImages(context)
        extractVoice(context)
        writeCore(context)
        AppState.cheatVersion = 1
        AppState.saveApp(context)
    }

    private fun clearCache(context: Context) {
        deleteDir(File(context.filesDir, "fonts"))
        deleteDir(File(context.filesDir, "image"))
        deleteDir(File(context.filesDir, "voice"))
    }

    private fun deleteDir(dir: File) {
        if (!dir.exists()) return
        dir.listFiles()?.forEach { child ->
            if (child.isDirectory) deleteDir(child) else child.delete()
        }
        dir.delete()
    }

    private fun extractImages(context: Context) {
        val outDir = File(context.filesDir, "image")
        if (!outDir.exists()) outDir.mkdirs()
        imageSets.forEach { (count, prefix) ->
            for (i in 1 until count) {
                val name = "$prefix$i.png"
                val out = File(outDir, name)
                if (out.exists()) continue
                runCatching {
                    context.assets.open("image/$name").use { input ->
                        out.outputStream().use { output -> input.copyTo(output) }
                    }
                }
            }
        }
    }

    private fun extractVoice(context: Context) {
        val outDir = File(context.filesDir, "voice")
        if (!outDir.exists()) outDir.mkdirs()
        val out = File(outDir, "expire")
        if (out.exists()) return
        runCatching {
            context.assets.open("voice/expire").use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
        }
    }

    private fun writeCore(context: Context) {
        FileHelper.writeCoreFile(context.filesDir, AppState.cheatVersion)
    }
}
