package com.example.ludostealth.screens
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import com.example.ludostealth.dialogs.LeaveDialog
import com.example.ludostealth.dialogs.SettingDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludostealth.R

@Composable
fun Mode(
    onComputerClick: () -> Unit,
    onLocalClick: () -> Unit,
    onHiddenTapSuccess: () -> Unit,
    onBackRequest: () -> Unit = {}
) {
    // ✅ Leave popup state
    val showLeaveDialog = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }
    // ✅ Hidden screen ke liye 5-tap logic
    var tapCount by remember { mutableStateOf(0) }
    var firstTapTime by remember { mutableStateOf(0L) }

    // ✅ 5 taps ko 2 seconds ke andar complete karna hoga
    val tapWindowMs = 2000L
// ✅ Activity reference (finishAffinity ke liye)
    val activity = LocalActivity.current
// ✅ System back button: agar dialog open nahi -> dialog open
    BackHandler(enabled = !showLeaveDialog.value) {
        showLeaveDialog.value = true
    }
    // ===== MAIN BACKGROUND =====
    // 🔧 EDIT: Yahan gradient colors change kar sakte ho
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val now = System.currentTimeMillis()

                        if (firstTapTime == 0L || now - firstTapTime > tapWindowMs) {
                            firstTapTime = now
                            tapCount = 1
                        } else {
                            tapCount++
                        }

                        if (tapCount >= 5) {
                            tapCount = 0
                            firstTapTime = 0L
                            onHiddenTapSuccess()
                        }
                    }
                )
            }
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2E2F6B),
                        Color(0xFF1C1D45)
                    )
                )
            )
    ) {

        // ===== TOP BAR BUTTONS =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // BACK BUTTON
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .shadow(10.dp, RoundedCornerShape(15.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF6BFF3B), Color(0xFF2E9C00))
                        ),
                        RoundedCornerShape(15.dp)
                    )
                    .clickable {
                        // 🔜 FUTURE: Back button logic yahan add hoga
                        showLeaveDialog.value = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // SETTINGS BUTTON
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .shadow(10.dp, RoundedCornerShape(15.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF6BFF3B), Color(0xFF2E9C00))
                        ),
                        RoundedCornerShape(15.dp)
                    )
                    .clickable {
                        // 🔜 FUTURE: Settings popup yahan open hoga
                        showSettingsDialog.value = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // ===== CENTER CONTENT =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ===== COMPUTER CARD =====
            ModeCard(
                imageRes = R.drawable.robot,
                title = "Computer",
                buttonColor = listOf(Color(0xFF9BFF00), Color(0xFF4CAF00)),
                onClick = { onComputerClick() }

            )

            Spacer(modifier = Modifier.height(40.dp))

            // ===== LOCAL CARD =====
            ModeCard(
                imageRes = R.drawable.local,
                title = "Local",
                buttonColor = listOf(Color(0xFFFF00D4), Color(0xFF9C27B0)),
                onClick = { onLocalClick() }
            )

            Spacer(modifier = Modifier.height(60.dp))

            // VERSION TEXT
            Text(
                text = "Version 1.1.2",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
    if (showLeaveDialog.value) {
        LeaveDialog(
            onYes = {
                activity?.finishAffinity()
            },
            onNo = {
                showLeaveDialog.value = false
            },
            onDismiss = {
                showLeaveDialog.value = false
            }
        )
    }

    if (showSettingsDialog.value) {
        SettingDialog(
            controlMenuMusic = true,
            onDismiss = {
                showSettingsDialog.value = false
            }
        )
    }
}
@Composable
fun ModeCard(
    imageRes: Int,
    title: String,
    buttonColor: List<Color>,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // IMAGE
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(220.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // BUTTON BAR
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(50.dp)
                .shadow(8.dp, RoundedCornerShape(15.dp))
                .background(
                    Brush.verticalGradient(buttonColor),
                    RoundedCornerShape(15.dp)
                )
                .clickable {
                    // 🔜 FUTURE: Navigate to next screen
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}