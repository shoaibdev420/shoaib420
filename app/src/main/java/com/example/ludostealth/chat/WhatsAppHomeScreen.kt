package com.example.ludostealth.chat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MoreVert
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

private val WaGreen = Color(0xFF25D366)
private val WaDark = Color(0xFF111B21)
private val WaSubtle = Color(0xFF667781)
private val WaBg = Color(0xFFF7F8FA)
private val WaSearch = Color(0xFFF0F2F5)
private val WaBorder = Color(0xFFDDE3E8)

private enum class HomeTab {
    CHATS, UPDATES, COMMUNITIES, CALLS
}
@Composable
fun WhatsAppHomeScreen(
    onOpenCamera: () -> Unit,
    onBack: () -> Unit,
    onSendMessageClick: ()-> Unit,
    onOpenSettings: () -> Unit
) {
    BackHandler {
        onBack()
    }

    var selectedTab by remember { mutableStateOf(HomeTab.CHATS) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WaBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WhatsApp",
                        color = Color(0xFF20B15A),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onOpenCamera() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Camera",
                            tint = WaDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "More options",
                                tint = WaDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("New group") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("New broadcast") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Linked devices") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Starred messages") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Payments") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    onOpenSettings()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(WaSearch)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = WaSubtle,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Ask Meta AI or Search",
                        color = Color(0xFF5F6368),
                        fontSize = 17.sp
                    )
                }
            }

            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(WaBg)
            ) {
                when (selectedTab) {
                    HomeTab.CHATS -> {
                        ChatsTabContent()
                    }

                    HomeTab.UPDATES -> {
                        UpdatesTabContent()
                    }

                    HomeTab.COMMUNITIES -> {
                        CommunitiesTabContent()
                    }

                    HomeTab.CALLS -> {
                        CallsTabContent()
                    }
                }

                if (selectedTab == HomeTab.CHATS) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFF2F2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "◌",
                                fontSize = 34.sp,
                                color = Color(0xFF7E57C2)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF20B15A))
                                .clickable {
                                    onSendMessageClick()
                                    // next step me Add Contacts screen khulega
                                }
                                .padding(horizontal = 22.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddComment,
                                contentDescription = "Send message",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Send message",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Bottom nav
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                HorizontalDivider(color = Color(0xFFEDEDED))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomTabItem(
                        label = "Chats",
                        isSelected = selectedTab == HomeTab.CHATS,
                        icon = Icons.Outlined.Chat,
                        onClick = { selectedTab = HomeTab.CHATS }
                    )

                    BottomTabItem(
                        label = "Updates",
                        isSelected = selectedTab == HomeTab.UPDATES,
                        icon = Icons.Outlined.Campaign,
                        showDot = true,
                        onClick = { selectedTab = HomeTab.UPDATES }
                    )

                    BottomTabItem(
                        label = "Communities",
                        isSelected = selectedTab == HomeTab.COMMUNITIES,
                        icon = Icons.Outlined.Groups,
                        onClick = { selectedTab = HomeTab.COMMUNITIES }
                    )

                    BottomTabItem(
                        label = "Calls",
                        isSelected = selectedTab == HomeTab.CALLS,
                        icon = Icons.Outlined.Call,
                        onClick = { selectedTab = HomeTab.CALLS }
                    )
                }

                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}
@Composable
private fun ChatsTabContent() {

    val chatList = remember { mutableStateListOf<Map<String, String>>() }

    LaunchedEffect(Unit) {

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .collection("chats")
                .get()
                .addOnSuccessListener { result ->

                    chatList.clear()

                    for (doc in result.documents) {

                        val data = mapOf(
                            "name" to (doc.getString("name") ?: ""),
                            "lastMessage" to (doc.getString("lastMessage") ?: "")
                        )

                        chatList.add(data)
                    }
                }
        }
    }

    if (chatList.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            Text(
                text = "Start chatting",
                color = WaDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chat with your contacts, or invite a\nfriend to WhatsApp.",
                color = WaSubtle,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    } else {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(chatList) { chat ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = chat["name"]?.take(1) ?: "",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                    ) {

                        Text(
                            text = chat["name"] ?: "",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WaDark
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = chat["lastMessage"] ?: "",
                            fontSize = 14.sp,
                            color = WaSubtle
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun UpdatesTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Updates",
            color = WaDark,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Status",
            color = WaDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD8E2DC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Y",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "My status",
                    color = WaDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tap to add status update",
                    color = WaSubtle,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CommunitiesTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Communities",
            color = WaDark,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "New community",
                    color = WaDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bring together a neighborhood, school, or more.",
                    color = WaSubtle,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CallsTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Calls",
            color = WaDark,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Create call link",
                    color = WaDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share a link for your WhatsApp call",
                    color = WaSubtle,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
private fun BottomTabItem(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showDot: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isSelected) Color(0xFFDDF7D8) else Color.Transparent
                    )
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = WaDark,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (showDot) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 6.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF20B15A))
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = WaDark,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}