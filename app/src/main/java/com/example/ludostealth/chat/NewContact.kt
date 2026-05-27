package com.example.ludostealth.chat

import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NcBg = Color(0xFFF7F8FA)
private val NcDark = Color(0xFF111B21)
private val NcSubtle = Color(0xFF667781)
private val NcBorder = Color(0xFFB0B7BD)
private val NcGreen = Color(0xFF25D366)

@Composable
fun NewContact(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("PK +92") }
    var phoneNumber by remember { mutableStateOf("") }
    var syncToPhone by remember { mutableStateOf(false) }
    var isRegisteredUser by remember { mutableStateOf(false) }
    var registeredUid by remember { mutableStateOf("") }

    val fullPhoneNumber = if (phoneNumber.startsWith("0")) {
        "+92" + phoneNumber.drop(1)
    } else {
        "+92$phoneNumber"
    }.replace(" ", "")
        .replace("-", "")

    LaunchedEffect(phoneNumber) {
        if (phoneNumber.length >= 11) {
            FirebaseFirestore.getInstance()
                .collection("registered_users")
                .whereEqualTo("phoneNumber", fullPhoneNumber)
                .get()
                .addOnSuccessListener { result ->

                    isRegisteredUser = !result.isEmpty

                    if (!result.isEmpty) {

                        registeredUid = result.documents[0]
                            .getString("uid") ?: ""
                    }
                }
        } else {
            isRegisteredUser = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NcBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = NcDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "New contact",
                    color = NcDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode2,
                        contentDescription = "QR",
                        tint = NcDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE7E7E7))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Person",
                    tint = NcSubtle,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "First name",
                                color = NcSubtle,
                                fontSize = 16.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NcBorder,
                            unfocusedBorderColor = NcBorder,
                            focusedTextColor = NcDark,
                            unfocusedTextColor = NcDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Last name",
                                color = NcSubtle,
                                fontSize = 16.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NcBorder,
                            unfocusedBorderColor = NcBorder,
                            focusedTextColor = NcDark,
                            unfocusedTextColor = NcDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = "Phone",
                    tint = NcSubtle,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.42f)
                                .height(64.dp)
                                .border(
                                    width = 1.dp,
                                    color = NcBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(Color.White, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = countryCode,
                                    color = NcDark,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Country",
                                    tint = Color(0xFF8A939A)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { newValue ->
                                phoneNumber = newValue.filter { it.isDigit() }.take(11)
                            },
                            modifier = Modifier.weight(0.58f),
                            placeholder = {
                                Text(
                                    text = "Phone",
                                    color = NcSubtle,
                                    fontSize = 16.sp
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NcBorder,
                                unfocusedBorderColor = NcBorder,
                                focusedTextColor = NcDark,
                                unfocusedTextColor = NcDark,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                    if (phoneNumber.length >= 11) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isRegisteredUser)
                                "This phone number is on WhatsApp."
                            else
                                "This phone number is not on WhatsApp.",
                            color = if (isRegisteredUser) NcGreen else NcSubtle,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = "Sync",
                    tint = NcSubtle,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "Sync contact to phone",
                    color = NcDark,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = syncToPhone,
                    onCheckedChange = { syncToPhone = it }
                )
            }
        }

        Button(
            onClick = {
                if (firstName.trim().isEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter first name",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                if (phoneNumber.trim().isEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                if (!isRegisteredUser) {
                    Toast.makeText(
                        context,
                        "This phone number is not on WhatsApp",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    Toast.makeText(
                        context,
                        "User not logged in",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val db = FirebaseFirestore.getInstance()

                val contactData = hashMapOf(
                    "uid" to registeredUid,
                    "firstName" to firstName.trim(),
                    "lastName" to lastName.trim(),
                    "phoneNumber" to fullPhoneNumber.trim(),
                    "countryCode" to countryCode,
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users")
                    .document(user.uid)
                    .collection("contacts")
                    .add(contactData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Contact saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        onSaveSuccess()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to save contact",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NcGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Save",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}