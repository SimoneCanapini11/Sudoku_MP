package com.canapini_grasselli.app_sudoku.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.canapini_grasselli.app_sudoku.model.AppTheme
import androidx.core.content.edit

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTheme(theme: AppTheme) {
        prefs.edit { putString(KEY_THEME, theme.name) }
    }

    fun getTheme(): AppTheme {
        val themeName = prefs.getString(KEY_THEME, AppTheme.PURPLE.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.PURPLE.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.PURPLE
        }
    }

    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME = "app_theme"
    }
}