package com.example.ludostealth.chat
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
fun ProfileScreen(
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("Hey there! I am using WhatsApp.") }
    var photoUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid ?: return@LaunchedEffect

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {

                userName = it.getString("name") ?: ""

                val savedAbout = it.getString("about")

                savedAbout?.let {
                    if (it.isNotEmpty()) {
                        about = it
                    }
                }

                photoUrl = it.getString("photoUrl") ?: ""
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    onBack()
                }
            ) {

                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                    tint = Color.Black
                )
            }

            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PROFILE IMAGE
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCE3EA)),

                contentAlignment = Alignment.Center
            ) {

                if (photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "",
                        modifier = Modifier.size(180.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = 60.dp, y = 60.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366)),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Edit",
            color = Color(0xFF25D366),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(35.dp))

        // NAME SECTION
        ProfileItem(
            icon = Icons.Outlined.AccountCircle,
            title = "Name",
            value = userName,
            showEdit = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 80.dp)
        )

        // ABOUT SECTION
        ProfileItem(
            icon = Icons.Outlined.Info,
            title = "About",
            value = about,
            showEdit = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 80.dp)
        )

        // PHONE
        ProfileItem(
            icon = Icons.Outlined.Call,
            title = "Phone",
            value = currentUser?.phoneNumber ?: "",
            showEdit = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 80.dp)
        )

        // LINKS
        ProfileItem(
            icon = Icons.Outlined.Link,
            title = "Links",
            value = "Add links",
            showAdd = true
        )
    }
}

@Composable
fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    showEdit: Boolean = false,
    showAdd: Boolean = false
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 22.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = "",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                color = if (title == "Links")
                    Color(0xFF25D366)
                else
                    Color.DarkGray
            )
        }

        if (showEdit) {

            Icon(
                Icons.Outlined.Edit,
                contentDescription = "",
                tint = Color(0xFF25D366)
            )
        }

        if (showAdd) {

            Icon(
                Icons.Outlined.Add,
                contentDescription = "",
                tint = Color(0xFF25D366)
            )
        }
    }
}