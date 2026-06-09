package com.example.ludostealth.chat
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ludostealth.chat.Message
import androidx.compose.foundation.layout.imePadding
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    name: String,
    receiverId: String,
    onBack: () -> Unit
) {

    BackHandler { onBack() }

    var messageText by remember { mutableStateOf("") }
    val currentUser = FirebaseAuth.getInstance().currentUser


    val chatId = if (currentUser!!.uid < receiverId) {
        currentUser.uid + "_" + receiverId
    } else {
        receiverId + "_" + currentUser.uid
    }
    val db = FirebaseFirestore.getInstance()
    val messages = remember { mutableStateListOf<Message>() }
    LaunchedEffect(Unit) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                if (value != null) {
                    messages.clear()

                    for (doc in value.documents) {

                        val msg = doc.toObject(Message::class.java)

                        if (msg != null) {

                            messages.add(msg)

                            if (
                                msg.receiverId == currentUser?.uid &&
                                msg.status == "delivered"
                            ) {

                                doc.reference.update(
                                    "status",
                                    "seen"
                                )
                            }
                        }
                    }
                }
            }
    }
    LaunchedEffect(messages.size) {

        messages.forEach { msg ->

            if (
                msg.receiverId == currentUser?.uid &&
                msg.status == "sent"
            ) {

                db.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .whereEqualTo("timestamp", msg.timestamp)
                    .get()
                    .addOnSuccessListener { docs ->

                        for (doc in docs) {

                            doc.reference.update(
                                "status",
                                "delivered"
                            )
                        }
                    }
            }
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
    ) {

        // 🔝 TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF075E54))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, "", tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1), color = Color.White)
            }

            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(name, color = Color.White, fontSize = 16.sp)
                Text("online", color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Videocam, "", tint = Color.White)
            }

            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Call, "", tint = Color.White)
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Outlined.MoreVert, "", tint = Color.White)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("View contact") }, onClick = {})
                    DropdownMenuItem(text = { Text("Media") }, onClick = {})
                    DropdownMenuItem(text = { Text("Search") }, onClick = {})
                    DropdownMenuItem(text = { Text("Wallpaper") }, onClick = {})
                }
            }
        }

        // 🧱 CHAT LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        ) {
            items(messages) { msg ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (msg.senderId == currentUser?.uid) Arrangement.End else Arrangement.Start
                ) {

                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (msg.senderId == currentUser?.uid) Color(0xFFDCF8C6)
                                else Color.White
                            )
                            .padding(12.dp)
                            .widthIn(0.dp, 250.dp)
                    ) {

                        Text(
                            text = msg.text,
                            color = if (msg.senderId == currentUser?.uid) Color.Black else Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    .format(Date(msg.timestamp)),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )

                            if (msg.senderId == currentUser?.uid) {

                                val tickIcon =
                                    if (msg.status == "sent") Icons.Default.Done
                                    else Icons.Default.DoneAll

                                val tickColor =
                                    if (msg.status == "seen") Color.Blue
                                    else Color.Gray

                                Icon(
                                    imageVector = tickIcon,
                                    contentDescription = "",
                                    tint = tickColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ⌨️ INPUT BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(6.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { }) {
                Icon(Icons.Default.EmojiEmotions, "", tint = Color.Gray)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                IconButton(onClick = { }) {
                    Icon(Icons.Default.AttachFile, "", tint = Color.Gray)
                }

                IconButton(onClick = { }) {
                    Icon(Icons.Default.CameraAlt, "", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (messageText.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, "", tint = Color.White)
                }
            } else {
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {

                            val msg = Message(
                                senderId = currentUser?.uid ?: "",
                                receiverId = receiverId,
                                text = messageText,
                                timestamp = System.currentTimeMillis(),
                                status = "sent"
                            )

                            db.collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .document()
                                .set(msg)
                                .addOnSuccessListener {

                                    messageText = ""

                                    // WhatsApp Home Chat Update
                                    val chatSummary = hashMapOf(
                                        "uid" to receiverId,
                                        "name" to name,
                                        "lastMessage" to msg.text,
                                        "timestamp" to msg.timestamp
                                    )

                                    // Sender side
                                    val senderChat = hashMapOf(
                                        "uid" to receiverId,
                                        "name" to name,
                                        "lastMessage" to msg.text,
                                        "timestamp" to msg.timestamp
                                    )

                                    db.collection("users")
                                        .document(currentUser!!.uid)
                                        .collection("chats")
                                        .document(receiverId)
                                        .set(senderChat)


// Receiver side
                                    val receiverChat = hashMapOf(
                                        "uid" to currentUser.uid,
                                        "name" to (currentUser.displayName ?: "User"),
                                        "lastMessage" to msg.text,
                                        "timestamp" to msg.timestamp
                                    )

                                    db.collection("users")
                                        .document(receiverId)
                                        .collection("chats")
                                        .document(currentUser.uid)
                                        .set(receiverChat)
                                }
                                .addOnFailureListener { e ->

                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    }

                }
            }
        }
    }
}