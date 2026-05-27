package com.example.ludostealth.screens

import com.example.ludostealth.logic.MenuMusic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Splash(
    // ✅ FUTURE: Next screen open karni ho to onFinish call hoga
    // Abhi MainActivity se call karenge, later NavGraph se.
    onFinish: (() -> Unit)? = null
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        MenuMusic.start (context)
    }
    // ✅ 2 seconds wait
    // 🔧 EDIT: delay(2000) change karke 1500 / 3000 etc. kar sakte ho
    LaunchedEffect(Unit) {
        delay(2000)
        onFinish?.invoke() // ✅ jab next screen ready ho, yahan call hoga
    }

    // ✅ Background glow animation (light pulsing)
    // 🔧 EDIT: tween(1200) speed change, alpha values change
    val t = rememberInfiniteTransition(label = "glow")
    val glow by t.animateFloat(
        initialValue = 0.25f,   // glow kam
        targetValue = 0.55f,    // glow zyada
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // ✅ MAIN BACKGROUND
            // 🔧 EDIT: yahan colors change karke background change karna hai
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF070A18), // top dark
                        Color(0xFF111D4A), // middle blue
                        Color(0xFF0B0F26)  // bottom dark
                    )
                )
            )
    ) {

        // ✅ Glow blob 1 (top-left)
        // 🔧 EDIT: size/offset/color/blur change kar sakte ho
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopStart)
                .offset((-80).dp, (-60).dp)
                .clip(CircleShape)
                .background(Color(0xFF6A5BFF).copy(alpha = glow))
                .blur(50.dp)
        )

        // ✅ Glow blob 2 (bottom-right)
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset((90).dp, (90).dp)
                .clip(CircleShape)
                .background(Color(0xFF00D4FF).copy(alpha = glow * 0.9f))
                .blur(60.dp)
        )

        // ✅ Center glass card
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                // 🔧 EDIT: glass alpha change (0.08f) to 0.12f etc.
                .background(Color.White.copy(alpha = 0.08f))
                .padding(vertical = 34.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ✅ Fake "3D orb" logo
            // 🔧 EDIT: size change, gradient colors change
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color(0xFF2BD9FF).copy(alpha = 0.7f),
                                Color(0xFF0A1030)
                            )
                        )
                    )
            )

            Spacer(Modifier.height(18.dp))

            // ✅ Main Title Text
            // 🔧 EDIT: "Welcome Baby" text change yahan se
            Text(
                text = "Welcome Baby",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            // ✅ Sub text
            Text(
                text = "Loading…",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // ✅ Bottom small app name
        // 🔧 EDIT: yahan se text change/ remove
        Text(
            text = "LudoStealth",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp
        )
    }
}