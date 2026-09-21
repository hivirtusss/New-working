package com.hivirtus.floatingmenu.util

import java.io.File

object FileHelper {
    fun coreFile(dir: File): File = File(dir, "CF")

    fun hasCoreFile(dir: File): Boolean = coreFile(dir).exists()

    fun writeCoreFile(dir: File, version: Int) {
        coreFile(dir).writeText("hivirtus_core_v$version\n")
    }

    fun deleteCoreFile(dir: File) {
        coreFile(dir).delete()
    }

    fun writeRootFile(content: String, path: String) {
        val escaped = content.replace("'", "'\"'\"'")
        ShellUtils.runShell("printf '%s' '$escaped' > '$path'")
    }
}
