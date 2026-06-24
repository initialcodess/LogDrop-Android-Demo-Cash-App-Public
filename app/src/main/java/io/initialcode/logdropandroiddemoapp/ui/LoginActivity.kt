package io.initialcode.logdropandroiddemoapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.initialcode.logdropandroiddemoapp.R
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropAndroidDemoAppTheme
import io.initialcode.logdropandroiddemoapp.utils.APIClient
import io.initialcode.logdropandroiddemoapp.utils.CacheManager
import io.initialcode.logdropandroiddemoapp.utils.LogDropLogger
import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

private const val TAG = "LoginActivity"

class LoginActivity : ComponentActivity() {

    private lateinit var cacheManager: CacheManager
    private val kUsernameKey = "cachedUsername"

    private val loginFlow = LogFlow(
        name = "LoginFlow",
        id = UUID.randomUUID().toString(),
        customAttributes = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cacheManager = CacheManager.getInstance(this)

        if (cacheManager.getString("access_token") != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            LogDropAndroidDemoAppTheme {
                LoginScreen(
                    cacheManager = cacheManager,
                    loginFlow = loginFlow,
                    onLoginSuccess = {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    cacheManager: CacheManager,
    loginFlow: LogFlow,
    onLoginSuccess: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiClient = remember { APIClient.getInstance(context) }

    LaunchedEffect(Unit) {
        LogDropLogger.logInfo(TAG, "Login screen opened")
        cacheManager.getString("cachedUsername")?.let {
            userName = it
            LogDropLogger.logInfo(TAG, "Loaded saved username: $it", loginFlow)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            )

            Text("Fast and Secure Payments", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                    LogDropLogger.logInfo(TAG, "Username updated: $it", loginFlow)
                },
                label = { Text("User Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = pinCode,
                onValueChange = {
                    pinCode = it
                    LogDropLogger.logInfo(TAG, "PIN code updated: password=$it", loginFlow)
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true
                    LogDropLogger.logInfo(TAG, "Sign in attempt for username: $userName", loginFlow)

                    coroutineScope.launch {
                        try {
                            val requestBody = JSONObject().apply {
                                put("username", userName)
                                put("password", pinCode)
                            }
                            val response = apiClient.request("/auth/login", "POST", requestBody)
                            val token = response.getString("access_token")
                            val responseUsername = response.optString("username", userName)

                            cacheManager.setString("access_token", token)
                            cacheManager.setString("cachedUsername", responseUsername)

                            LogDrop.userUpdate(userId = responseUsername)
                            LogDropLogger.logInfo(TAG, "Sign in successful for username: $responseUsername", loginFlow)

                            onLoginSuccess()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = e.message ?: "Invalid username or password"
                            LogDropLogger.logWarning(TAG, "Sign in failed for username: $userName: ${e.message}", loginFlow)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && userName.isNotEmpty() && pinCode.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign In")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}