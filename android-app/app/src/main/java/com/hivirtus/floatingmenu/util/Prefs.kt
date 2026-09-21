package com.hivirtus.floatingmenu.util

import android.content.Context

object Prefs {
    private const val NAME = "hivirtus_prefs"

    fun getUser(context: Context): String =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString("user", "") ?: ""

    fun getPassword(context: Context): String =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString("password", "") ?: ""

    fun saveLogin(context: Context, user: String, password: String) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("user", user)
            .putString("password", password)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getUser(context).isNotBlank() && getPassword(context).isNotBlank()
    }
}
