package io.initialcode.logdropandroiddemoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.initialcode.logdropandroiddemoapp.ui.theme.LogDropBlue
import io.initialcode.logdropandroiddemoapp.ui.theme.White
import io.initialcode.logdropandroiddemoapp.utils.APIClient
import io.initialcode.logdropandroiddemoapp.utils.LogDropLogger
import io.logdrop.sdk.LogFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "SendFundsSheet"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendFundsSheet(
    initialUsername: String,
    onDismiss: () -> Unit,
    onTransferSuccess: () -> Unit,
    sendFundsFlow: LogFlow
) {
    var username by remember { mutableStateOf(initialUsername) }
    var message by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiClient = remember { APIClient.getInstance(context) }

    ModalBottomSheet(
        onDismissRequest = {
            LogDropLogger.logInfo(TAG, "Send Funds sheet closed", sendFundsFlow)
            onDismiss()
        },
        containerColor = White,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text("✕", color = LogDropBlue, fontSize = 20.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("Send Funds", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(24.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("USERNAME", fontSize = 12.sp, color = Color.Gray)
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Enter username", color = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    enabled = !isProcessing
                )
            }

            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Add a message", color = Color.Gray) },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                enabled = !isProcessing
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("AMOUNT", fontSize = 12.sp, color = Color.Gray)
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = {
                        Text(
                            "$0.00",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isProcessing
                )
            }

            errorMessage?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = error, color = Color.Red, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    isProcessing = true
                    errorMessage = null
                    LogDropLogger.logInfo(
                        TAG,
                        "Send Funds confirmed → username=$username, amount=$amount, message=$message",
                        sendFundsFlow
                    )

                    coroutineScope.launch {
                        try {
                            val amountVal = amount.toDoubleOrNull()?.toInt() ?: 0
                            val requestBody = JSONObject().apply {
                                put("receiverUsername", username)
                                put("amount", amountVal)
                                put("message", message)
                            }
                            apiClient.request("/transactions/transfer", "POST", requestBody)
                            LogDropLogger.logInfo(TAG, "Transfer to $username successful", sendFundsFlow)
                            onTransferSuccess()
                        } catch (e: Exception) {
                            isProcessing = false
                            errorMessage = e.message ?: "An error occurred during transfer"
                            LogDropLogger.logError(TAG, "Transfer failed: ${e.message}", sendFundsFlow)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LogDropBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing && username.isNotEmpty() && amount.isNotEmpty()
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Send ${if (amount.isEmpty()) "$0.00" else "$$amount"}", color = White)
                }
            }
        }
    }
}
