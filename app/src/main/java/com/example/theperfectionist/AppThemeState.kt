package com.example.theperfectionist

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppThemeState {
    private const val PREF_NAME = "app_theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    var darkMode by mutableStateOf(false)

    fun loadTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        darkMode = prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        darkMode = enabled

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    val backgroundColor: Color
        get() = if (darkMode) Color(0xFF1E1E1E) else Color(0xFFA2CCFF).copy(alpha = 0.85f)

    val topBarColor: Color
        get() = if (darkMode) Color(0xFF2B2B2B) else Color(0xFF8DBEF8).copy(alpha = 0.85f)

    val cardColor: Color
        get() = if (darkMode) Color(0xFF2F2F2F) else Color(0xFFEAF4FF)

    val textColor: Color
        get() = if (darkMode) Color.White else Color.Black

    val subTextColor: Color
        get() = if (darkMode) Color.LightGray else Color.DarkGray

    val buttonColor: Color
        get() = if (darkMode) Color(0xFF666666) else Color(0xFF03DAC5).copy(alpha = 0.15f)

    val buttonBorderColor: Color
        get() = if (darkMode) Color(0xFFAAAAAA) else Color(0xFF009688).copy(alpha = 0.4f)

    val purpleColor: Color
        get() = if (darkMode) Color(0xFFB39DDB) else Color(0xFF6F49B5)
}