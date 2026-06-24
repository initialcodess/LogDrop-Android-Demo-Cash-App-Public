package io.initialcode.logdropandroiddemoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.initialcode.logdropandroiddemoapp.R
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropAndroidDemoAppTheme
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropBlue
import io.initialcode.logdropandroiddemoapp.ui.theme.White
import io.initialcode.logdropandroiddemoapp.utils.APIClient
import io.initialcode.logdropandroiddemoapp.utils.DummyData
import io.initialcode.logdropandroiddemoapp.utils.LogDropLogger
import org.json.JSONObject
import java.lang.Exception

//
//  HomeActivity.kt
//  LogDropAndroidDemoApp
//
//  Copyright (c) 2025 LogDrop.
//  @author Initial Code Software Solutions
//

private val TAG = "HomeActivity"

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogDropAndroidDemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    HomeView()
                }
            }
        }
    }
}

@Composable
fun HomeView() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = LogDropBlue,
        unselectedIconColor = Color.Gray,
        selectedTextColor = LogDropBlue,
        unselectedTextColor = Color.Gray,
        indicatorColor = Color.Transparent
    )

    Scaffold(
        containerColor = White,
        bottomBar = {
            NavigationBar(containerColor = White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        LogDropLogger.logInfo(TAG, "Home screen opened")
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    },
                    label = { Text("Home") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        LogDropLogger.logDebug(TAG, "Payments screen opened")
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_payments),
                            contentDescription = "Payments",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    },
                    label = { Text("Payments") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        LogDropLogger.logWarning(TAG, "Exit tapped, logging out")
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_exit),
                            contentDescription = "Exit",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    },
                    label = { Text("Exit") },
                    colors = navItemColors
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeTab(Modifier.padding(paddingValues))
            1 -> PaymentsView(Modifier.padding(paddingValues))
            2 -> ExitView(Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun HomeTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val apiClient = remember { APIClient.getInstance(context) }

    var balance by remember { mutableStateOf("$0.00") }
    var cardNumber by remember { mutableStateOf("**** **** **** ****") }
    var cardHolder by remember { mutableStateOf("-") }
    var expiryDate by remember { mutableStateOf("--/--") }
    var accountNumber by remember { mutableStateOf("•••• ••••") }
    var transactions by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        LogDropLogger.logInfo(TAG, "Home screen opened")
        try {
            val response = apiClient.request("/dashboard", "GET")
            val account = response.getJSONObject("account")
            balance = "$" + account.getString("balance")
            accountNumber = "•••• " + account.getString("accountNumber").takeLast(4)

            val cards = response.optJSONArray("cards")
            if (cards != null && cards.length() > 0) {
                val card = cards.getJSONObject(0)
                cardNumber = card.getString("cardNumber")
                cardHolder = card.getString("cardHolder")
                expiryDate = card.getString("expiryDate")
            }

            val txArray = response.optJSONArray("transactions")
            if (txArray != null) {
                val list = mutableListOf<JSONObject>()
                for (i in 0 until txArray.length()) {
                    list.add(txArray.getJSONObject(i))
                }
                transactions = list
            }
            isLoading = false
            LogDropLogger.logInfo(TAG, "Dashboard data loaded successfully")
        } catch (e: Exception) {
            isLoading = false
            LogDropLogger.logError(TAG, "Dashboard request failed: ${e.message}")
        }
    }

    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LogDropBlue)
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            LogDropLogger.logInfo(TAG, "User tapped on profile icon")
                            throw RuntimeException("unexpected nil value while loading profile") // CRASH
                        }
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = LogDropBlue,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            LogDropLogger.logDebug(TAG, "Settings button tapped")
                        }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CURRENT ACCOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = balance,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = Color.Black
                    )

                    LaunchedEffect(balance) {
                        LogDropLogger.logInfo(TAG, "Balance displayed: $balance")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Black, LogDropBlue)
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = cardNumber,
                                color = White
                            )
                            Text(
                                text = cardHolder,
                                color = White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = expiryDate,
                                color = White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.ic_vector_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(width = 50.dp, height = 50.dp)
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_creditcard),
                            contentDescription = "Credit Card",
                            modifier = Modifier.size(24.dp),
                            tint = LogDropBlue
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "Current Account",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = accountNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                LogDropLogger.logInfo(TAG, "Manage button tapped")
                                simulateApiCall()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LogDropBlue.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Manage",
                                color = LogDropBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )

                    LaunchedEffect(Unit) {
                        LogDropLogger.logDebug(TAG, "Transactions list appeared")
                    }

                    TextButton(
                        onClick = {
                            LogDropLogger.logInfo(TAG, "See All transactions tapped")
                        }
                    ) {
                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelSmall,
                            color = LogDropBlue
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    transactions.forEach { tx ->
                        val type = tx.optString("type", "EXPENSE")
                        val isExpense = type == "EXPENSE"
                        val iconRes = if (isExpense) R.drawable.ic_subs else R.drawable.ic_salary
                        val amountText = if (isExpense) "-$" + tx.optString("amount") else "+$" + tx.optString("amount")
                        val amountColor = if (isExpense) Color.Black else Color(0xFF4CAF50)

                        TransactionRow(
                            iconRes = iconRes,
                            title = tx.optString("title"),
                            type = type,
                            amount = amountText,
                            amountColor = amountColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TransactionRow(
    iconRes: Int,
    title: String,
    type: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = LogDropBlue
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Text(
                text = type,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = amount,
            color = amountColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExitView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                LogDropLogger.logWarning(TAG, "Exit tapped, logging out")
                context.getSharedPreferences("logdrop_cache", Context.MODE_PRIVATE).edit().remove("access_token").apply()

                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = LogDropBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Exit", color = White)
        }
    }
}

private fun simulateApiCall() {
    LogDropLogger.logDebug(TAG, "API request started: POST /v1/account/manage")
    Handler(Looper.getMainLooper()).postDelayed({
        LogDropLogger.logError(TAG, "API request failed: ${DummyData.failedApiResponse}")
    }, 1500)
}
