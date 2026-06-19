package io.initialcode.logdropandroiddemoapp

import android.app.Application
import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogDropConfig
import org.json.JSONObject

class LogDropAndroidDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Register activity lifecycle callbacks for session/push tracking
        LogDrop.registerActivityLifecycleCallback(this)

        val configBuilder = LogDropConfig.Builder()
            .logcatEnabled(true)

        val configData = loadLogDropConfig()
        if (configData != null) {
            val (appId, baseUrl) = configData
            if (baseUrl.isNotEmpty()) {
                configBuilder.baseUrl(baseUrl)
            }
            configBuilder.appId(appId)
        }

        LogDrop.init(this, configBuilder.build())
    }

    private fun loadLogDropConfig(): Pair<String, String>? {
        return try {
            val jsonString = assets.open("logdrop-services.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val baseUrl = jsonObject.optString("base_url", "")
            val projects = jsonObject.optJSONObject("projects")
            val project = projects?.optJSONObject(packageName)
            val appId = project?.optString("app_id", "") ?: project?.optString("api_key", "") ?: ""
            if (appId.isNotEmpty()) {
                Pair(appId, baseUrl)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
