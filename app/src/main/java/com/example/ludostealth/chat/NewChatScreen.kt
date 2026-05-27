package com.example.ludostealth.chat
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChatBg = Color(0xFFF7F8FA)
private val ChatDark = Color(0xFF111B21)
private val ChatSubtle = Color(0xFF667781)
private val ChatSearch = Color(0xFFF0F2F5)
private val ChatGreen = Color(0xFF25D366)

@Composable
fun NewChatScreen(
    onBack: () -> Unit,
    onNewGroupClick: () -> Unit,
    onNewContactClick: () -> Unit,
    onNewCommunityClick: () -> Unit,
    onContactClick: (Map<String, String>) -> Unit
) {
    BackHandler {
        onBack()
    }

    var showMenu by remember { mutableStateOf(false) }
    val contactList = remember { mutableStateListOf<Map<String, String>>() }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                .document(user.uid)
                .collection("contacts")
                .get()
                .addOnSuccessListener { result ->
                    contactList.clear()
                    for (doc in result) {
                        val data = mapOf(
                            "uid" to (doc.getString("uid") ?: ""),
                            "firstName" to (doc.getString("firstName") ?: ""),
                            "lastName" to (doc.getString("lastName") ?: ""),
                            "phoneNumber" to (doc.getString("phoneNumber") ?: "")
                        )
                        contactList.add(data)
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.padding(top = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ChatDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "New chat",
                    color = ChatDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable { showMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More",
                            tint = ChatDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Invite a friend") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Contacts") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Refresh") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Help") },
                            onClick = { showMenu = false }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ChatSearch)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To:",
                    color = ChatDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "  Search name or number",
                    color = Color(0xFF5F6368),
                    fontSize = 17.sp
                )
            }

            HorizontalDivider(color = Color(0xFFE7E7E7))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            NewChatOptionRow(
                icon = Icons.Outlined.Groups,
                title = "New group",
                trailingIcon = null,
                onClick = onNewGroupClick
            )

            NewChatOptionRow(
                icon = Icons.Outlined.PersonAdd,
                title = "New contact",
                trailingIcon = Icons.Outlined.QrCode2,
                onClick = onNewContactClick
            )

            NewChatOptionRow(
                icon = Icons.Outlined.Groups,
                title = "New community",
                trailingIcon = null,
                onClick = onNewCommunityClick
            )

            Text(
                text = "Contacts on WhatsApp",
                color = ChatSubtle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp)
            )

            contactList.forEach { contact ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onContactClick(contact)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ChatSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (contact["firstName"] ?: "").take(1),
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = (contact["firstName"] ?: "") + " " + (contact["lastName"] ?: ""),
                            fontSize = 16.sp,
                            color = ChatDark
                        )

                        Text(
                          text = contact["phoneNumber"] ?: "",
                          fontSize = 13.sp,
                          color = ChatSubtle
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun NewChatOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(ChatGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = title,
            color = ChatDark,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = "$title QR",
                tint = ChatDark,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}