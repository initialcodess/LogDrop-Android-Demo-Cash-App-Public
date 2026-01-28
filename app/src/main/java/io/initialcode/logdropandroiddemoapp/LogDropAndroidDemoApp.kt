package io.initialcode.logdropandroiddemoapp

import android.app.Application
import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogDropConfig

class LogDropAndroidDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = LogDropConfig.Builder()
            .apiKey(BuildConfig.LOGDROP_API_KEY)
            .baseUrl(BuildConfig.LOGDROP_BASE_URL)
            .logcatEnabled(true)
            .crashTrackingEnabled(true)
            .sensitiveInfoFilter(
                listOf(
                    Regex("^cardNo:\\s(?:\\d{4}[-\\s]?){3}\\d{4}$"),
                    Regex("password=[^&\\s]+")
                )
            )
            .crashTrackingEnabled(true)
            .build()

        LogDrop.init(this, config)
    }
}
