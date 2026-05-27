package com.example.ludostealth.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable

/**
 * ✅ Leave Game popup (3D-ish + animation)
 *
 * onYes   -> app close (finishAffinity) will be called from Mode.kt
 * onNo    -> just close dialog
 * onDismiss -> outside tap / back press close
 */
@Composable
fun LeaveDialog(
    onYes: () -> Unit,
    onNo: () -> Unit,
    onDismiss: () -> Unit
) {
    // ✅ Small open animation (scale + fade)
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.90f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnClickOutside = true, // ✅ outside tap -> close
            dismissOnBackPress = true,    // ✅ system back -> close (when dialog open)
            usePlatformDefaultWidth = false
        )
    ) {
        // Full screen overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                // ✅ dark overlay (EDIT: overlay darkness)
                .background(Color(0xAA000000))
                // NOTE: Dialog already dismisses on outside click, but this makes whole overlay clickable too.
                .clickable(onClick = { onDismiss() }),
            contentAlignment = Alignment.Center
        ) {
            // Main card (glass-ish)
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .alpha(alpha.value)
                    .shadow(18.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            // ✅ EDIT: dialog panel colors here
                            colors = listOf(Color(0xFF2B3F4A), Color(0xFF1E2E36)),
                            start = Offset(0f, 0f),
                            end = Offset(900f, 900f)
                        )
                    )
                    .padding(20.dp)
                    // stop overlay click propagation
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Title
                    Text(
                        text = "Leave Game?",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(22.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // YES button (red)
                        Action3DButton(
                            text = "YES",
                            // ✅ EDIT: YES colors
                            gradient = listOf(Color(0xFFB84B4B), Color(0xFF8E2E2E)),
                            onClick = onYes
                        )

                        // NO button (green)
                        Action3DButton(
                            text = "NO",
                            // ✅ EDIT: NO colors
                            gradient = listOf(Color(0xFF7ED12B), Color(0xFF2E8E1F)),
                            onClick = onNo
                        )
                    }
                }
            }
        }
    }
}

/**
 * ✅ Reusable 3D-ish button (gradient + shadow)
 *
 * EDIT points:
 * - width/height
 * - corner radius
 * - gradient colors
 */
@Composable
private fun Action3DButton(
    text: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)      // ✅ EDIT: button width
            .height(62.dp)      // ✅ EDIT: button height
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradient))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}