package io.initialcode.logdropandroiddemoapp

import android.app.Application
import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogDropConfig

class LogDropAndroidDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = LogDropConfig.Builder()
            .logcatEnabled(true)
            .build()

        LogDrop.init(this, config)
    }
}
