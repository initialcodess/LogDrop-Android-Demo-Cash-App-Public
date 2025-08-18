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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.initialcode.logdropandroiddemoapp.R
import io.initialcode.logdropandroiddemoapp.ui.HomeActivity
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropAndroidDemoAppTheme
import io.initialcode.logdropandroiddemoapp.utils.CacheManager
import io.initialcode.logdropandroiddemoapp.utils.DummyData
import io.initialcode.logdropandroiddemoapp.utils.LogDropLogger
import io.logdrop.sdk.LogDrop
import io.logdrop.sdk.LogFlow
import java.util.*

//
//  LoginActivity.kt
//  LogDropAndroidDemoApp
//
//  Copyright (c) 2025 LogDrop.
//  @author Initial Code
//
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
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        LogDropLogger.logInfo("LoginActivity", "Login screen opened")
        cacheManager.getString("cachedUsername")?.let {
            userName = it
            LogDropLogger.logInfo("LoginActivity", "Loaded saved username: $it", loginFlow)
        }
    }

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
                LogDropLogger.logInfo("LoginActivity", "Username updated: $it", loginFlow)
            },
            label = { Text("User Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pinCode,
            onValueChange = {
                pinCode = it
                // The "password" value here will be masked in logs
                // because of the SensitiveInfoFilter added in LogDropConfig
                LogDropLogger.logInfo("LoginActivity", "PIN code updated: password=$it", loginFlow)
            },
            label = { Text("PIN Code (1234)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Text(
                text = "Invalid credentials",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                LogDropLogger.logInfo("LoginActivity", "Sign in attempt for username: $userName", loginFlow)
                if (pinCode == DummyData.pinCode) {
                    showError = false
                    val cachedUser = cacheManager.getString("cachedUsername")

                    if (cachedUser != userName) {
                        cacheManager.setString("cachedUsername", userName)
                        LogDrop.userUpdate(userId = userName)
                        LogDropLogger.logInfo("LoginActivity", "Sign in successful for username: $userName", loginFlow)
                    } else {
                        LogDropLogger.logInfo("LoginActivity", "Sign in successful (cached user reused): $userName", loginFlow)
                    }

                    onLoginSuccess()
                } else {
                    showError = true
                    LogDropLogger.logWarning("LoginActivity", "Sign in failed for username: $userName", loginFlow)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}