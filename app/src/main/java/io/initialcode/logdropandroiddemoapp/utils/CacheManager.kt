package io.initialcode.logdropandroiddemoapp.utils

//
//  CacheManager.kt
//  LogDropAndroidDemoApp
//
//  Copyright (c) 2025 LogDrop.
//  @author Initial Code
//

import android.content.Context
import android.content.SharedPreferences

class CacheManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("logdrop_cache", Context.MODE_PRIVATE)

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }

    fun setBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBool(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun setInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun setLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheManager? = null

        fun getInstance(context: Context): CacheManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
