package com.example.ludostealth.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onProfileClick: () -> Unit
) {

    BackHandler {
        onBack()
    }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val phone = currentUser?.phoneNumber ?: ""

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        item {

            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // PROFILE SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onProfileClick()
                    }
                    .padding(horizontal = 18.dp, vertical = 16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD8E2DC)),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "S",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Stealth User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = phone,
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }

                Icon(
                    Icons.Outlined.QrCode,
                    contentDescription = "",
                    tint = Color(0xFF25D366)
                )
            }

            HorizontalDivider()

            SettingsItem(
                icon = Icons.Outlined.Key,
                title = "Account",
                subtitle = "Security notifications, change number"
            )

            SettingsItem(
                icon = Icons.Outlined.Lock,
                title = "Privacy",
                subtitle = "Blocked contacts, disappearing messages"
            )

            SettingsItem(
                icon = Icons.Outlined.People,
                title = "Avatar",
                subtitle = "Create, edit, profile photo"
            )

            SettingsItem(
                icon = Icons.Outlined.Chat,
                title = "Chats",
                subtitle = "Theme, wallpapers, chat history"
            )

            SettingsItem(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                subtitle = "Message, group & call tones"
            )

            SettingsItem(
                icon = Icons.Outlined.Storage,
                title = "Storage and data",
                subtitle = "Network usage, auto-download"
            )

            SettingsItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help",
                subtitle = "Help center, contact us"
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = "",
            tint = Color.Gray,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {

            Text(
                text = title,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}