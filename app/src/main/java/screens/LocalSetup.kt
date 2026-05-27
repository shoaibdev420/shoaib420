package com.example.ludostealth.screens
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ✅ LocalSetup Screen (single file)
 *
 * - Top: SELECT PLAYERS (2P, 3P, 4P) with tick mark
 * - Bottom: 4 color cards with Human/Nobody
 * - Auto logic:
 *    2P => Green+Blue Human, Red+Yellow Nobody
 *    3P => Green+Red+Blue Human, Yellow Nobody
 *    4P => all Human
 * - User can manually tap any card to toggle Human/Nobody
 * - Back button + System back => onBack()
 * - Play button => onPlay(players, greenHuman, redHuman, yellowHuman, blueHuman)
 */
@Composable
fun LocalSetup(
    onBack: () -> Unit,
    onPlay: (
        players: Int,
        greenHuman: Boolean,
        redHuman: Boolean,
        yellowHuman: Boolean,
        blueHuman: Boolean
    ) -> Unit,
    onHiddenTapSuccess: () -> Unit
) {

    // =======================
    // ✅ 1) PLAYER SELECTION
    // =======================
    var players by remember { mutableIntStateOf(2) } // ✅ default 2P

    // =======================
    // ✅ 2) HUMAN/NOBODY STATE
    // =======================
    var greenHuman by remember { mutableStateOf(true) }
    var redHuman by remember { mutableStateOf(false) }
    var yellowHuman by remember { mutableStateOf(false) }
    var blueHuman by remember { mutableStateOf(true) }

    /**
     * ✅ Auto-set on players change (exactly like you asked)
     * NOTE: User can still change after auto-set.
     */
    LaunchedEffect(players) {
        when (players) {
            2 -> {
                // ✅ 2P => Green + Blue Human
                greenHuman = true
                blueHuman = true
                redHuman = false
                yellowHuman = false
            }

            3 -> {
                // ✅ 3P => Green + Red + Blue Human
                greenHuman = true
                redHuman = true
                blueHuman = true
                yellowHuman = false
            }

            4 -> {
                // ✅ 4P => all Human
                greenHuman = true
                redHuman = true
                yellowHuman = true
                blueHuman = true
            }
        }
    }
    // ✅ Hidden chat ke liye 5-tap logic
    var tapCount by remember { mutableStateOf(0) }
    var firstTapTime by remember { mutableStateOf(0L) }

    // ✅ 5 taps ko 2 seconds ke andar complete karna hoga
    val tapWindowMs = 2000L
    // ✅ System back button also works
    BackHandler { onBack() }

    // =======================
    // ✅ Background small sparkle animation (simple)
    // =======================
    val t = rememberInfiniteTransition(label = "sparkle")
    val sparkleAlpha by t.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )

    // =======================
    // ✅ MAIN SCREEN
    // =======================
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
                    colors = listOf(
                        Color(0xFF2E2F6B),
                        Color(0xFF1C1D45)
                    )
                )
            )
    )
    {

        // ✅ Soft bokeh dots / sparkles (pure code)
        SparkleLayer(alpha = sparkleAlpha)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =======================
            // ✅ TOP PANEL (PLAYERS)
            // =======================
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "SELECT PLAYERS",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFE06B) // 🔧 EDIT: title color
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PlayerPick(
                            label = "2P",
                            selected = (players == 2),
                            onClick = { players = 2 }
                        )
                        PlayerPick(
                            label = "3P",
                            selected = (players == 3),
                            onClick = { players = 3 }
                        )
                        PlayerPick(
                            label = "4P",
                            selected = (players == 4),
                            onClick = { players = 4 }
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // =======================
            // ✅ BOTTOM PANEL (4 CARDS)
            // =======================
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "SELECT COLOR",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFE06B)
                    )

                    Spacer(Modifier.height(20.dp))

                    // ✅ 2x2 cards exactly like image layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ColorHumanCard(
                            colorName = "GREEN",
                            // 🔧 EDIT: green colors
                            cardColors = listOf(Color(0xFF2FAE43), Color(0xFF1B7A2B)),
                            human = greenHuman,
                            onToggle = { greenHuman = !greenHuman }
                        )
                        ColorHumanCard(
                            colorName = "RED",
                            cardColors = listOf(Color(0xFFB02B3B), Color(0xFF7C1D28)),
                            human = redHuman,
                            onToggle = { redHuman = !redHuman }
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ColorHumanCard(
                            colorName = "YELLOW",
                            cardColors = listOf(Color(0xFFC3A130), Color(0xFF8B7421)),
                            human = yellowHuman,
                            onToggle = { yellowHuman = !yellowHuman }
                        )
                        ColorHumanCard(
                            colorName = "BLUE",
                            cardColors = listOf(Color(0xFF2E7DD2), Color(0xFF1D4F8A)),
                            human = blueHuman,
                            onToggle = { blueHuman = !blueHuman }
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // =======================
            // ✅ Bottom Buttons Row
            // =======================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ✅ Back button (left) - same green style
                GreenPillButton(
                    text = "◀",
                    width = 78.dp,
                    onClick = onBack
                )

                // ✅ Play button (center)
                GreenPillButton(
                    text = "PLAY",
                    width = 210.dp,
                    onClick = {
                        // ✅ logic abhi placeholder — tum baad me game start karwana
                        onPlay(players, greenHuman, redHuman, yellowHuman, blueHuman)
                    }
                )

                Spacer(Modifier.width(4.dp)) // small spacing (right side empty like image)
            }
        }
    }
}

/* =========================================================================================
   ✅ UI PIECES (same file)
   ========================================================================================= */

@Composable
private fun GlassPanel(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(18.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            // 🔧 EDIT: panel glass color
            .background(Color(0xFF3A3B73).copy(alpha = 0.65f))
    ) {
        // ✅ gloss highlight overlay (makes it look more 3D)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 900f)
                    )
                )
        )
        content()
    }
}

@Composable
private fun PlayerPick(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        // ✅ small square with border and tick (like image)
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(10.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2C2D55).copy(alpha = 0.65f))
                .clickable { onClick() }
        ) {
            // border glow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF7EE7FF).copy(alpha = 0.35f),
                                Color(0xFFFF79D1).copy(alpha = 0.35f)
                            )
                        )
                    )
            )

            // inner
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(7.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFF2C2D55))
            )

            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = label,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ColorHumanCard(
    colorName: String,
    cardColors: List<Color>,
    human: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(155.dp)
            .height(120.dp)
            .shadow(14.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(cardColors))
            .clickable { onToggle() }
    ) {
        // ✅ diagonal shine
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.12f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(650f, 650f)
                    )
                )
        )

        // ✅ face circle (Human) like image feeling (no extra image needed)
        if (human) {
            HumanFaceIcon(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
                    .size(48.dp)
            )
        }

        Text(
            text = if (human) "Human" else "Nobody",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        )
    }
}

@Composable
private fun GreenPillButton(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(64.dp)
            .shadow(16.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    // 🔧 EDIT: green button gradient
                    colors = listOf(Color(0xFF7CFF2F), Color(0xFF2E9C00))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = if (text == "PLAY") 26.sp else 26.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SparkleLayer(alpha: Float) {
    // simple dots to make background alive (pure code)
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // soft circles
        drawCircle(Color.White.copy(alpha = 0.07f), radius = 70f, center = Offset(size.width * 0.15f, size.height * 0.20f))
        drawCircle(Color.White.copy(alpha = 0.05f), radius = 90f, center = Offset(size.width * 0.80f, size.height * 0.35f))
        drawCircle(Color.White.copy(alpha = 0.06f), radius = 60f, center = Offset(size.width * 0.25f, size.height * 0.75f))
        drawCircle(Color.White.copy(alpha = 0.04f), radius = 110f, center = Offset(size.width * 0.70f, size.height * 0.80f))

        // tiny sparkles
        fun sparkle(x: Float, y: Float) {
            val c = Color(0xFFFFF19A).copy(alpha = alpha)
            drawCircle(c, radius = 5f, center = Offset(x, y))
            drawCircle(c, radius = 2f, center = Offset(x + 10f, y + 6f))
        }

        sparkle(size.width * 0.20f, size.height * 0.10f)
        sparkle(size.width * 0.55f, size.height * 0.25f)
        sparkle(size.width * 0.85f, size.height * 0.15f)
        sparkle(size.width * 0.12f, size.height * 0.55f)
        sparkle(size.width * 0.92f, size.height * 0.62f)
        sparkle(size.width * 0.35f, size.height * 0.92f)
    }
}

@Composable
private fun HumanFaceIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        // face circle
        drawCircle(Color.White.copy(alpha = 0.95f))
        // outline
        drawCircle(Color.Black.copy(alpha = 0.25f), style = Stroke(width = 4f))

        // hair (simple arc shape)
        val hair = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.40f)
            quadraticBezierTo(size.width * 0.50f, size.height * 0.05f, size.width * 0.82f, size.height * 0.40f)
            quadraticBezierTo(size.width * 0.50f, size.height * 0.25f, size.width * 0.18f, size.height * 0.40f)
            close()
        }
        drawPath(hair, Color.Black.copy(alpha = 0.85f))

        // eyes
        drawCircle(Color.Black.copy(alpha = 0.70f), radius = 6f, center = Offset(size.width * 0.38f, size.height * 0.55f))
        drawCircle(Color.Black.copy(alpha = 0.70f), radius = 6f, center = Offset(size.width * 0.62f, size.height * 0.55f))

        // mouth
        drawCircle(Color.Black.copy(alpha = 0.40f), radius = 3.5f, center = Offset(size.width * 0.50f, size.height * 0.70f))
    }
}