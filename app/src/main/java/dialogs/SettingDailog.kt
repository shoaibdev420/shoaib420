package com.example.ludostealth.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ludostealth.logic.MenuMusic

/**
 * Abhi ke liye temporary in-memory settings state
 * App band hogi to reset ho jayega
 */
object GameSettingsState {
    var soundOn by mutableStateOf(true)
    var notificationOn by mutableStateOf(true)
}

@Composable
fun SettingDialog(
    controlMenuMusic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2F5F8F),
                                Color(0xFF1F4268),
                                Color(0xFF173553)
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFF4E7CB5),
                        shape = RoundedCornerShape(26.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 18.dp, start = 18.dp, end = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // =========================
                    // HEADER
                    // =========================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.88f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFD7F15D),
                                            Color(0xFF8EBC1F)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Settings",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 10.dp, y = (-8).dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFFFB347),
                                            Color(0xFFE24A1A)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = CircleShape
                                )
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "X",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // =========================
                    // SOUND
                    // =========================
                    SettingRow(
                        title = "Sound",
                        trailing = {
                            GameStyleToggle(
                                checked = GameSettingsState.soundOn,
                                onCheckedChange = { newValue ->
                                    GameSettingsState.soundOn = newValue
                                    if (controlMenuMusic) {
                                        if (newValue) {
                                            MenuMusic.start(context)
                                        } else {
                                            MenuMusic.stop()
                                        }
                                    }
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // =========================
                    // NOTIFICATION
                    // =========================
                    SettingRow(
                        title = "Notification",
                        trailing = {
                            GameStyleToggle(
                                checked = GameSettingsState.notificationOn,
                                onCheckedChange = { newValue ->
                                    GameSettingsState.notificationOn = newValue
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // =========================
                    // MORE SETTINGS
                    // =========================
                    SettingRow(
                        title = "More Settings",
                        trailing = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF1B4D7F),
                                                Color(0xFF123A63)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = Color.White.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        Toast.makeText(
                                            context,
                                            "Coming Soon",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ">",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    trailing: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF24496B),
                        Color(0xFF1A3855)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            trailing()
        }
    }
}

@Composable
private fun GameStyleToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val bgBrush = if (checked) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF74E63A),
                Color(0xFF2EAA1A)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFB33A3A),
                Color(0xFF7A1717)
            )
        )
    }

    Box(
        modifier = Modifier
            .width(94.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(50))
            .background(bgBrush)
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(50)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 6.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = if (checked) "ON" else "OFF",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFE05D),
                            Color(0xFFFF8A50)
                        )
                    )
                )
        )
    }
}