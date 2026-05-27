package com.example.ludostealth.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onClose: () -> Unit,
    onVerifyOtp: (String) -> Unit
) {
    BackHandler {
        onClose()
    }

    var otpCode by remember { mutableStateOf("") }

    val isVerifyEnabled = otpCode.length == 6

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top bar
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF111B21)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title
            Text(
                text = "Verifying your number",
                color = Color(0xFF111B21),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Subtitle
            Text(
                text = "You've tried to register $phoneNumber\nrecently. Wait before requesting\nan sms or a call with your code.",
                color = Color(0xFF667781),
                fontSize = 15.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // OTP input
            TextField(
                value = otpCode,
                onValueChange = { newValue ->
                    otpCode = newValue.filter { it.isDigit() }.take(6)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF111B21),
                    fontSize = 22.sp
                ),
                placeholder = {
                    Text(
                        text = "Enter 6-digit code",
                        color = Color(0xFF8696A0),
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,

                    focusedIndicatorColor = Color(0xFF25D366),
                    unfocusedIndicatorColor = Color(0xFF25D366),
                    disabledIndicatorColor = Color(0xFF25D366),
                    errorIndicatorColor = Color.Red,

                    focusedTextColor = Color(0xFF111B21),
                    unfocusedTextColor = Color(0xFF111B21),
                    disabledTextColor = Color(0xFF111B21),

                    focusedPlaceholderColor = Color(0xFF8696A0),
                    unfocusedPlaceholderColor = Color(0xFF8696A0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Waiting to automatically detect an SMS sent to $phoneNumber",
                color = Color(0xFF25D366),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onVerifyOtp(otpCode)
                },
                enabled = isVerifyEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366),
                    disabledContainerColor = Color(0xFFE9EDEF),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFAEBAC1)
                )
            ) {
                Text(
                    text = "Verify",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
