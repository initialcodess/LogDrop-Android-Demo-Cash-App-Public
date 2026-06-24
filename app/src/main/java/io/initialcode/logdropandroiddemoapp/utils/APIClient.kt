package io.initialcode.logdropandroiddemoapp.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class APIClient private constructor(context: Context) {
    private val cacheManager = CacheManager.getInstance(context)
    private val baseURL = "https://cashapp-demo.logdrop.io"

    suspend fun request(
        path: String,
        method: String = "GET",
        body: JSONObject? = null
    ): JSONObject = withContext(Dispatchers.IO) {
        val url = URL("$baseURL$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

        val token = cacheManager.getString("access_token")
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer $token")
        }

        if (body != null && (method == "POST" || method == "PUT")) {
            conn.doOutput = true
            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body.toString())
            writer.flush()
            writer.close()
        }

        val statusCode = conn.responseCode
        if (statusCode == 401) {
            LogDropLogger.logError("APIClient", "Unauthorized request to $path")
            throw BackendException("Unauthorized")
        }

        if (statusCode in 200..299) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            JSONObject(responseText)
        } else {
            val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }
                ?: conn.inputStream?.bufferedReader()?.use { it.readText() }
                ?: "Unknown error"
            conn.disconnect()

            val message = try {
                JSONObject(errorText).optString("message", "An unknown error occurred")
            } catch (e: Exception) {
                errorText
            }
            LogDropLogger.logError("APIClient", "Request to $path failed with status $statusCode: $message")
            throw BackendException(message)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: APIClient? = null

        fun getInstance(context: Context): APIClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: APIClient(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

class BackendException(message: String) : Exception(message)
