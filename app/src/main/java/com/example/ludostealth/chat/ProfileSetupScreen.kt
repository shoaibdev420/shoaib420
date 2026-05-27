package com.example.ludostealth.chat

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

@Composable
fun ProfileSetupScreen(
    onClose: () -> Unit,
    onProfileSaved: () -> Unit
) {
    BackHandler {
        onClose()
    }

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    // ===============================
    // UI states
    // ===============================
    var profileName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCameraBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var showMenuDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // ===============================
    // Gallery picker
    // ===============================
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedCameraBitmap = null
        }
    }

    // ===============================
    // Camera picker
    // NOTE:
    // Ye thumbnail bitmap return karta hai.
    // Profile picture ke liye enough hai.
    // ===============================
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedCameraBitmap = bitmap
            selectedImageUri = null
        }
    }

    // ===============================
    // Basic validation
    // ===============================
    val isNextEnabled = profileName.trim().length >= 2 && !isSaving

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
            // Title
            // ===============================
            Text(
                text = "Profile info",
                color = Color(0xFF111B21),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Please provide your name and an optional\nprofile photo",
                color = Color(0xFF667781),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(34.dp))

            // ===============================
            // Profile image area
            // ===============================
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(150.dp)
            ) {
                // Main profile circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFE9EDEF))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected profile photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        selectedCameraBitmap != null -> {
                            Image(
                                bitmap = selectedCameraBitmap!!.asImageBitmap(),
                                contentDescription = "Captured profile photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        else -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default profile",
                                tint = Color(0xFF8696A0),
                                modifier = Modifier.size(74.dp)
                            )
                        }
                    }
                }

                // Small green camera button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add profile photo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            // ===============================
            // Name input row
            // ===============================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = profileName,
                        onValueChange = { newValue ->
                            // Name max 25 chars for clean UI
                            profileName = newValue.take(25)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF111B21),
                            fontSize = 18.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Type your name here",
                                color = Color(0xFF8696A0),
                                fontSize = 18.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
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

                IconButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Emoji button later add karenge",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.InsertEmoticon,
                        contentDescription = "Emoji",
                        tint = Color(0xFF8696A0)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ===============================
            // Bottom Next Button
            // ===============================
            Button(
                onClick = {
                    val currentUser = auth.currentUser
                    if (currentUser == null) {
                        Toast.makeText(
                            context,
                            "User not logged in",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (profileName.trim().length < 2) {
                        Toast.makeText(
                            context,
                            "Please enter a valid name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isSaving = true

                    val uid = currentUser.uid
                    val phone = currentUser.phoneNumber
                        ?.replace(" ", "")
                        ?.replace("-", "")
                        .orEmpty()

                    // ✅ Abhi temporary image upload OFF hai
                    // Sirf basic profile Firestore me save hogi
                    val userMap = hashMapOf(
                        "uid" to uid,
                        "name" to profileName.trim(),
                        "phone" to phone,
                        "photoUrl" to "",
                        "profileCompleted" to true,
                        "createdAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener {

                            val registeredUserMap = hashMapOf(
                                "uid" to uid,
                                "name" to profileName.trim(),
                                "phoneNumber" to phone,
                                "createdAt" to System.currentTimeMillis()
                            )

                            firestore.collection("registered_users")
                                .document(uid)
                                .set(registeredUserMap)
                                .addOnSuccessListener {
                                    isSaving = false
                                    Toast.makeText(
                                        context,
                                        "Profile saved successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onProfileSaved()
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    Toast.makeText(
                                        context,
                                        e.message ?: "Failed to register user",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            isSaving = false
                            Toast.makeText(
                                context,
                                e.message ?: "Failed to save profile",
                                Toast.LENGTH_LONG
                            ).show()
                        }
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
                    text = if (isSaving) "Saving..." else "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }

        // ===============================
        // Source picker dialog
        // ===============================
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = {
                    Text(
                        text = "Select profile photo",
                        color = Color(0xFF111B21)
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                showImageSourceDialog = false
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Choose from gallery",
                                color = Color(0xFF111B21)
                            )
                        }

                        TextButton(
                            onClick = {
                                showImageSourceDialog = false
                                cameraLauncher.launch(null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Take photo from camera",
                                color = Color(0xFF111B21)
                            )
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {},
                containerColor = Color.White
            )
        }

        // ===============================
        // 3-dot menu dialog
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