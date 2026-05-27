package com.example.ludostealth.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import com.example.ludostealth.screens.PawnColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ✅ Simple enum for color selection (easy to read + future editing)

@Composable
fun ComputerSetup(
    onBack: () -> Unit,
    onPlay: (players: Int, color: PawnColor) -> Unit
) {
    // ✅ DEFAULT selection (tumne bola: 2P default)
    var selectedPlayers by remember { mutableIntStateOf(2) }

    // ✅ DEFAULT color (tumne bola: jo marzi select ho, default green ok)
    var selectedColor by remember { mutableStateOf(PawnColor.GREEN) }

    // ===== MAIN BACKGROUND (EDIT yahan se background colors change kar sakte ho) =====
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2E2F6B), // top
                        Color(0xFF1C1D45)  // bottom
                    )
                )
            )
    ) {

        // ===== CONTENT COLUMN =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // ===== CARD 1: SELECT PLAYERS =====
            GlassCard {
                Text(
                    text = "SELECT PLAYERS",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFE36B) // EDIT: title color
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerPick(
                        label = "2P",
                        selected = (selectedPlayers == 2),
                        onClick = { selectedPlayers = 2 }
                    )
                    PlayerPick(
                        label = "3P",
                        selected = (selectedPlayers == 3),
                        onClick = { selectedPlayers = 3 }
                    )
                    PlayerPick(
                        label = "4P",
                        selected = (selectedPlayers == 4),
                        onClick = { selectedPlayers = 4 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ===== CARD 2: SELECT COLOR =====
            GlassCard {
                Text(
                    text = "SELECT COLOR",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFE36B)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ✅ 2x2 grid style
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ColorPick(
                            colorName = "Green",
                            color = Color(0xFF3DFF57),
                            selected = selectedColor == PawnColor.GREEN,
                            onClick = { selectedColor = PawnColor.GREEN }
                        )
                        ColorPick(
                            colorName = "Red",
                            color = Color(0xFFFF4D4D),
                            selected = selectedColor == PawnColor.RED,
                            onClick = { selectedColor = PawnColor.RED }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ColorPick(
                            colorName = "Yellow",
                            color = Color(0xFFFFD54A),
                            selected = selectedColor == PawnColor.YELLOW,
                            onClick = { selectedColor = PawnColor.YELLOW }
                        )
                        ColorPick(
                            colorName = "Blue",
                            color = Color(0xFF4D8DFF),
                            selected = selectedColor == PawnColor.BLUE,
                            onClick = { selectedColor = PawnColor.BLUE }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ===== BOTTOM BUTTONS =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // BACK button
                Green3DButton(
                    text = "",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    },
                    width = 86.dp,
                    onClick = onBack
                )

                // PLAY button
                Green3DButton(
                    text = "PLAY",
                    icon = null,
                    width = 190.dp,
                    onClick = {
                        // ✅ abhi bas next screen open hoga
                        // ✅ future: yahan actual game start logic aayega
                        onPlay(selectedPlayers, selectedColor)
                    }
                )
            }
        }
    }
}

/* ------------------------------ UI HELPERS (same file) ------------------------------ */

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(22.dp))
            .background(
                color = Color(0xFF5A5B85).copy(alpha = 0.55f), // EDIT: card glass color
                shape = RoundedCornerShape(22.dp)
            )
            .padding(vertical = 22.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun PlayerPick(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1.0f, label = "playerScale")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
                .shadow(10.dp, RoundedCornerShape(12.dp))
                .background(
                    color = Color(0xFF3B3C5D),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun ColorPick(
    colorName: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.08f else 1.0f, label = "colorScale")
    val glowAlpha by animateFloatAsState(if (selected) 1f else 0f, label = "glowAlpha")

    Box(
        modifier = Modifier
            .size(92.dp)
            .scale(scale)
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .background(Color(0xFF3B3C5D), RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // glow border effect (selected)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .alpha(glowAlpha)
                .background(color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
        )

        // "pawn" simple circle (future me image replace kar sakte ho)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color, CircleShape)
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .background(Color(0xFFFFD54A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF1C1D45),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun Green3DButton(
    text: String,
    icon: (@Composable (() -> Unit))?,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(58.dp)
            .shadow(14.dp, RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF79FF2D), Color(0xFF2E9C00)) // EDIT: button colors
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            icon()
        } else {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}