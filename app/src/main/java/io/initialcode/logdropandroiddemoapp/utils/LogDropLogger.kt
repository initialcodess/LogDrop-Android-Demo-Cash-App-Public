package io.initialcode.logdropandroiddemoapp.utils

import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogFlow

/**
 * LogDropLogger.kt
 * LogDropAndroidDemoApp
 * Copyright (c) 2025 LogDrop.
 * @author Initial Code
 *
 * This helper class manages LogFlow objects and provides simple logging methods.
 *
 * You can set a global LogFlow to be used across the app.
 * For one-off flows, you can pass a LogFlow object directly to the logging methods.
 *
 * This prevents creating a new flow ID for every log and keeps related logs grouped under the same flow.
 */
object LogDropLogger {

    private var currentLogFlow: LogFlow? = null

    fun setGlobalFlow(logFlow: LogFlow?) {
        currentLogFlow = logFlow
    }

    fun clearGlobalFlow() {
        currentLogFlow = null
    }

    fun logInfo(tag: String, message: String, logFlow: LogFlow? = null) {
        LogDrop.i(tag, message, logFlow ?: currentLogFlow)
    }

    fun logWarning(tag: String, message: String, logFlow: LogFlow? = null) {
        LogDrop.w(tag, message, logFlow ?: currentLogFlow)
    }

    fun logError(tag: String, message: String, logFlow: LogFlow? = null) {
        LogDrop.e(tag, message, logFlow ?: currentLogFlow)
    }

    fun logDebug(tag: String, message: String, logFlow: LogFlow? = null) {
        LogDrop.d(tag, message, logFlow ?: currentLogFlow)
    }
}
