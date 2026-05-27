package com.example.ludostealth.chat

import androidx.compose.material3.AlertDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===============================
// Hidden Chat Screen
// WhatsApp style first screen
// Abhi sirf UI hai, backend/OTP nahi

@Composable
fun HiddenChatScreen(
    onClose: () -> Unit,
    onSendOtp: (String) -> Unit
) {
    // Device back button bhi previous screen par le jayega
    BackHandler {
        onClose()
    }
    // Phone number ke liye local state
    // Abhi sirf typing ke liye, koi real validation nahi
    var phoneNumber by remember { mutableStateOf("") }

    // 3-dot menu ka simple placeholder
    var showMenuDialog by remember { mutableStateOf(false) }

    // Button enable tabhi hoga jab user kuch type kare
    // Future me is condition ko proper validation se replace karna
    val isNextEnabled = phoneNumber.length == 10
    // Firebase ko full international format chahiye
    val fullPhoneNumber = "+92$phoneNumber"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ===============================
            // Top Bar
            // Left: close icon
            // Right: 3 dots menu
            // ===============================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF111B21)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { showMenuDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color(0xFF111B21)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ===============================
            // Main Title
            // ===============================
            Text(
                text = "Enter your phone number",
                color = Color(0xFF111B21),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ===============================
            // Description Text
            // "What's my number?" green color me
            // ===============================
            Text(
                text = buildAnnotatedString {
                    append("WhatsApp will need to verify your phone number.\n")
                    append("Carrier charges may apply. ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF128C7E),
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("What's my number?")
                    }
                },
                color = Color(0xFF667781),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(34.dp))

            // ===============================
            // Country Section
            // Abhi static Pakistan rakha hai
            // Future me isko clickable dropdown bana sakte ho
            // ===============================
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pakistan",
                        color = Color(0xFF111B21),
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Country dropdown",
                        tint = Color(0xFF128C7E)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    thickness = 1.5.dp,
                    color = Color(0xFF25D366)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ===============================
            // Phone Number Input Row
            // Left: +92
            // Right: Phone number text field
            // ===============================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(0.28f)
                ) {
                    Text(
                        text = "+ 92",
                        color = Color(0xFF111B21),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    HorizontalDivider(
                        thickness = 1.5.dp,
                        color = Color(0xFF25D366)
                    )
                }

                Spacer(modifier = Modifier.weight(0.06f))

                Column(
                    modifier = Modifier.weight(0.66f)
                ) {
                    TextField(
                        value = phoneNumber,
                        onValueChange = { newValue ->
                            // Sirf digits allow karo
                            // Future me yahan proper formatting bhi add kar sakte ho
                            phoneNumber = newValue
                                .filter { it.isDigit() }
                                .take(10)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF111B21),
                            fontSize = 18.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Phone number",
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
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ===============================
            // Bottom Next Button
            // Abhi sirf UI hai
            // Future me yahan OTP / registration logic aayega
            // ===============================
            Button(
                onClick = {
                   onSendOtp(fullPhoneNumber)
                },
                enabled = isNextEnabled,
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
                    text = "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }

        // ===============================
        // Simple placeholder menu dialog
        // Abhi sirf look ke liye
        // Future me Help / Terms / Privacy add kar sakte ho
        // ===============================
        if (showMenuDialog) {
            AlertDialog(
                onDismissRequest = { showMenuDialog = false },
                confirmButton = {
                    TextButton(onClick = { showMenuDialog = false }) {
                        Text(
                            text = "Help",
                            color = Color(0xFF111B21)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMenuDialog = false }) {
                        Text(
                            text = "Privacy policy",
                            color = Color(0xFF111B21)
                        )
                    }
                },
                title = null,
                text = null,
                containerColor = Color.White
            )
        }
    }
}