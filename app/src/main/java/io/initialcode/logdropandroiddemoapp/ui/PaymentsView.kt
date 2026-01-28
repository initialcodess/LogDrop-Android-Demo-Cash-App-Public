package io.initialcode.logdropandroiddemoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.initialcode.logdropandroiddemoapp.R
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropBlue
import io.initialcode.logdropandroiddemoapp.ui.theme.White
import io.initialcode.logdropandroiddemoapp.utils.LogDropLogger
import io.logdrop.sdk.LogFlow
import java.util.*

private const val TAG = "PaymentsView"

@Composable
fun PaymentsView(modifier: Modifier = Modifier) {
    var flowUuid by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }

    val sendFundsFlow = remember {
        LogFlow(
            name = "SendFundsFlow",
            id = UUID.randomUUID().toString(),
            customAttributes = null
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = flowUuid,
                onValueChange = { flowUuid = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                placeholder = { Text("Set Flow UUID", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    /**
                    DEMO only:
                    This UI lets us set a global flow id for logs.
                    In real apps, flows are set by developers in code.
                    If input is empty, the global flow is cleared (nil).
                     */
                    if (flowUuid.trim().isEmpty()) {
                        LogDropLogger.setGlobalFlow(null)
                        LogDropLogger.logInfo(TAG, "Global flow cleared")
                    } else {
                        val newFlow = LogFlow(
                            name = flowUuid,
                            id = UUID.randomUUID().toString(),
                            customAttributes = null
                        )
                        LogDropLogger.setGlobalFlow(newFlow)
                        LogDropLogger.logInfo(TAG, "Global flow set with id: $flowUuid")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LogDropBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Set", color = White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
        ) {
            Text("ALL ACCOUNTS", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_creditcard),
                    contentDescription = "Accounts",
                    tint = LogDropBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "All Accounts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        "$7,325.29",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PaymentActionRow(
                icon = R.drawable.ic_send,
                title = "Send Funds",
                subtitle = "Via transfer or payment link",
                color = Color(0xFF4CAF50)
            ) {
                LogDropLogger.logInfo(TAG, "Send Funds tapped")
                showSheet = true
            }

            Divider(color = Color(0xFFE0E0E0))
            PaymentActionRow(
                icon = R.drawable.ic_receive,
                title = "Receive Funds",
                subtitle = "Request a payment from others",
                color = LogDropBlue
            ) {
                LogDropLogger.logError(TAG, "Receive Funds request failed: No valid account linked")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Pay Fast",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val users = listOf("Liam", "Olivia", "Noah", "Emma", "Mason")
                users.forEach { name ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            LogDropLogger.logInfo(TAG, "Pay Fast tapped for user: $name")
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(LogDropBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.first().toString(),
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(name, fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }
    }

    if (showSheet) {
        SendFundsSheet(
            onDismiss = { showSheet = false },
            sendFundsFlow = sendFundsFlow
        )
    }
}

@Composable
fun PaymentActionRow(
    icon: Int,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "Next",
            tint = Color.Gray
        )
    }
}
