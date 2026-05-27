package com.example.ludostealth.logic
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ludostealth.dialogs.SettingDialog
import com.example.ludostealth.logic.MenuMusic
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ludostealth.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
/**
 * STEP 1-3 STARTER:
 * ✅ Purple background
 * ✅ Back + Settings buttons
 * done Board later
 * ❌ Tokens later
 */
@Composable
fun GameBoardScreen(
    playersCount: Int,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onHiddenTapSuccess: () -> Unit
) {

    // =========================================
    // Active colors by selected players
    // 2P = RED + YELLOW
    // 3P = GREEN + RED + YELLOW
    // 4P = GREEN + RED + YELLOW + BLUE
    // =========================================
    // =====================================================
    // MAIN GAME STATE
    // =====================================================
    var gameState by remember(playersCount) {
        mutableStateOf(GameLogic.createInitialState(playersCount))
    }
    var showSettingsDialog by remember { mutableStateOf(false) }
    // ✅ Hidden chat ke liye 5-tap logic
    var tapCount by remember { mutableStateOf(0) }
    var firstTapTime by remember { mutableStateOf(0L) }

    // ✅ 5 taps ko 2 seconds ke andar complete karna hoga
    val tapWindowMs = 2000L
    LaunchedEffect(Unit) {
        MenuMusic.stop()
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        TokenSound.init(context)
    }

    // Animated temporary cells:
    // Jab token step-by-step move karega to thori dair yahan uski current moving cell rahegi
    val animatedCellByTokenId = remember { mutableStateMapOf<Int, Cell>() }

    val scope = rememberCoroutineScope()

    // =====================================================
    // DICE ROLL RESULT HANDLE
    // =====================================================
    fun handleDiceRolled(result: DiceRollResult) {
        gameState = GameLogic.onDiceRolled(
            state = gameState,
            rolledNumber = result.number
        )
    }

    // =====================================================
    // TOKEN CLICK HANDLE
    // =====================================================
    fun handleTokenClick(tokenId: Int) {
        if (!gameState.selectableTokenIds.contains(tokenId)) return

        scope.launch {
            val moveResult = GameLogic.onTokenSelected(
                state = gameState,
                tokenId = tokenId
            )

            // Step-by-step animation
            moveResult.movedPath.forEach { cell ->
                animatedCellByTokenId[tokenId] = cell
                TokenSound.play()
                delay(220)
                // 120 = fast
                // 180 = medium
                // 250 = slow
            }

            animatedCellByTokenId.remove(tokenId)
            gameState = moveResult.newState
        }
    }

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
            .background(Color(0xFF3B3382)) // Purple background
    )
    {
        // (Optional) just to see playersCount working
        Text(
            text = "Board Screen - Players: $playersCount | Turn: ${gameState.currentTurn} | Dice: ${gameState.lastDiceNumber ?: "-"}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )
        Image(
            painter = painterResource(R.drawable.ludo_board),
            contentDescription = "Ludo Board",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.98f)
                .aspectRatio(1f)
                .offset(y = 0.dp),
            contentScale = ContentScale.Fit
        )

        // ==============================
        // TOKENS OVERLAY
        // Same size + same position as board image
        // ==============================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.98f)   // same as board image
                .aspectRatio(1f)
                .offset(y = 0.dp)      // same as board image
        ) {
            AllTokensOverlay(
                gameState = gameState,
                animatedCellByTokenId = animatedCellByTokenId,
                onTokenClick = ::handleTokenClick
            )
        }

        // ==============================
        // DICE BOXES OVERLAY
        // Board ke around color boxes
        // ==============================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.98f)
                .aspectRatio(1f)
                .offset(y = 0.dp)
        ) {
            DiceBoxesOverlay(
                playersCount = playersCount,
                currentTurn = gameState.currentTurn,
                diceEnabled = gameState.lastDiceNumber == null,
                onDiceRolled = { result ->
                    handleDiceRolled(result)
                }
            )
        }
        // Bottom Back Button
        GreenSquareButton(
            text = "←",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 18.dp),
            onClick = onBack
        )

        // Bottom Settings Button
        GreenSquareButton(
            text = "⚙",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp),
            onClick = {
                showSettingsDialog = true
            }
        )
    }

    if (showSettingsDialog) {
        SettingDialog(
            controlMenuMusic = false,
            onDismiss = {
                showSettingsDialog = false
            }
        )
    }
}
@Composable
private fun GreenSquareButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF5CDD2E), Color(0xFF2DAF18))
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.25f), shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Black,
            fontSize = 45.sp
        )
    }
}

private object TokenPos {
    val green = listOf(
        0.135f to 0.140f,//top left
        0.263f to 0.140f,// top right
        0.135f to 0.265f,//bottom left
        0.263f to 0.265f//bottom right
    )

    val red = listOf(
        0.74f to 0.139f,//top left
        0.87f to 0.139f,//top right
        0.74f to 0.264f,//bottom left
        0.87f to 0.264f//bottom right
    )

    val yellow = listOf(
        0.135f to 0.72f,//top left
        0.263f to 0.72f,//top right
        0.135f to 0.84f,//bottom left
        0.263f to 0.84f//bottome right
    )

    val blue = listOf(
        0.74f to 0.72f,//top left
        0.87f to 0.72f,//top right
        0.74f to 0.84f,//bottom left
        0.87f to 0.84f//bottom right
    )
}

/**
 * =========================================================
 * BOARD TOKEN CELL ADJUST
 * =========================================================
 *
 * Agar path par chalte waqt token thoda cell se left/right/up/down lag raha ho
 * to yahan change karo.
 *
 * xAdjust:
 *   minus = left
 *   plus  = right
 *
 * yAdjust:
 *   minus = upar
 *   plus  = neeche
 */
private object BoardTokenAdjust {
    const val xAdjust = 0.0f
    const val yAdjust = 0.0f
}

/**
 * =========================================================
 * ALL TOKENS OVERLAY
 * =========================================================
 *
 * Ye function:
 * - home ke tokens dikhata hai
 * - board par chale hue tokens dikhata hai
 * - step-by-step moving token dikhata hai
 * - selectable token ko clickable banata hai
 */
@Composable
private fun AllTokensOverlay(
    gameState: LogicGameState,
    animatedCellByTokenId: Map<Int, Cell>,
    onTokenClick: (Int) -> Unit
) {
    val tokenSize = 30.dp

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val w = this@BoxWithConstraints.maxWidth
        val h = this@BoxWithConstraints.maxHeight

        @Composable
        fun placeToken(
            xFrac: Float,
            yFrac: Float,
            color: Color,
            tokenId: Int,
            isSelectable: Boolean
        ) {
            // =====================================================
            // SELECTABLE TOKEN ANIMATION
            // =====================================================
            // Agar token selectable hai to:
            // - thora uper neeche move karega
            // - neeche black rotating ring chalegi
            val infinite = rememberInfiniteTransition(label = "token_select_animation")

            // Uper neeche bob movement
            val bobY = if (isSelectable) {
                infinite.animateFloat(
                    initialValue = 0f,
                    targetValue = -6f,   // EDIT: zyada upar karna ho to -8f ya -10f
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 450, // EDIT: bob speed
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "token_bob_y"
                ).value
            } else {
                0f
            }

            // Black ring rotation
            val ringRotation = if (isSelectable) {
                infinite.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 900, // EDIT: ring ghoomne ki speed
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ring_rotation"
                ).value
            } else {
                0f
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = (w * xFrac) - (tokenSize / 2),
                        y = (h * yFrac) - (tokenSize / 2)
                    )
                    .graphicsLayer {
                        translationY = bobY
                    }
                    .size(tokenSize)
                    .clickable(enabled = isSelectable) {
                        onTokenClick(tokenId)
                    },
                contentAlignment = Alignment.Center
            ) {
                // =====================================================
                // BLACK ROTATING RING
                // Sirf selectable token ke liye
                // =====================================================
                if (isSelectable) {
                    Canvas(
                        modifier = Modifier.size(tokenSize)
                    ) {
                        val s = size.minDimension
                        val cx = size.width / 2f
                        val cy = size.height / 2f + s * 0.18f   // ring token ke neeche

                        rotate(ringRotation, pivot = Offset(cx, cy)) {
                            repeat(8) { i ->
                                rotate(i * 45f, pivot = Offset(cx, cy)) {
                                    drawRoundRect(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        topLeft = Offset(
                                            x = cx - s * 0.045f,
                                            y = cy - s * 0.36f
                                        ),
                                        size = Size(
                                            width = s * 0.09f,
                                            height = s * 0.16f
                                        ),
                                        cornerRadius = CornerRadius(
                                            x = s * 0.03f,
                                            y = s * 0.03f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // =====================================================
                // MAIN TOKEN
                // =====================================================
                PawnToken(
                    color = color,
                    size = tokenSize,
                    isSelected = false
                )
            }
        }
        // =====================================================
        // DRAW ORDER FIX
        // =====================================================
        // Rule:
        // - selectable token sabse last draw hoga
        // - isse same cell me current movable token front par aayega
        // - aur touch bhi wahi receive karega
        val orderedTokens = gameState.tokens.sortedBy { token ->
            if (gameState.selectableTokenIds.contains(token.id)) 1 else 0
        }
        orderedTokens.forEach { token ->
            val isSelectable = gameState.selectableTokenIds.contains(token.id)

            // -------------------------------------------------
            // 1) Agar token step-by-step move ho raha hai
            // -------------------------------------------------
            val animatedCell = animatedCellByTokenId[token.id]
            if (animatedCell != null) {
                val (x, y) = animatedCell.toFraction()

                placeToken(
                    xFrac = x + BoardTokenAdjust.xAdjust,
                    yFrac = y + BoardTokenAdjust.yAdjust,
                    color = token.movement.color.toUiColor(),
                    tokenId = token.id,
                    isSelectable = false
                )
                return@forEach
            }

            // -------------------------------------------------
            // 2) Home tokens
            // -------------------------------------------------
            if (token.movement.isAtHome) {
                val slotIndex = tokenSlotIndex(token.id)

                val (x, y) = when (token.movement.color) {
                    PlayerColor.GREEN -> TokenPos.green[slotIndex]
                    PlayerColor.RED -> TokenPos.red[slotIndex]
                    PlayerColor.YELLOW -> TokenPos.yellow[slotIndex]
                    PlayerColor.BLUE -> TokenPos.blue[slotIndex]
                }

                placeToken(
                    xFrac = x,
                    yFrac = y,
                    color = token.movement.color.toUiColor(),
                    tokenId = token.id,
                    isSelectable = isSelectable
                )
                return@forEach
            }

            // -------------------------------------------------
            // 3) Finished token
            // Abhi isko hide rakha hai
            // Future me center finish area me dikha sakte ho
            // -------------------------------------------------
            if (token.movement.isFinished) {
                return@forEach
            }

            // -------------------------------------------------
            // 4) Normal board / home lane token
            // -------------------------------------------------
            val currentCell = GameLogic.currentCellOf(token.movement)
            if (currentCell != null) {
                val (baseX, baseY) = currentCell.toFraction()
                val (stackX, stackY) = stackAdjust(token.id)

                placeToken(
                    xFrac = baseX + BoardTokenAdjust.xAdjust + stackX,
                    yFrac = baseY + BoardTokenAdjust.yAdjust + stackY,
                    color = token.movement.color.toUiColor(),
                    tokenId = token.id,
                    isSelectable = isSelectable
                )
            }
        }
    }
}

/**
 * Token id se home slot nikalta hai
 *
 * Example:
 * 101 -> 0
 * 102 -> 1
 * 103 -> 2
 * 104 -> 3
 */
private fun tokenSlotIndex(tokenId: Int): Int {
    return (tokenId % 100) - 1
}

/**
 * Same cell par multiple tokens overlap na hon
 * isliye thoda visual stack offset
 *
 * Agar tumhe same-cell overlap pasand nahi to
 * yahan values zero kar sakte ho.
 */
private fun stackAdjust(tokenId: Int): Pair<Float, Float> {
    return 0f to 0f
}

/**
 * Path color ko UI token color me convert karne ke liye
 */
private fun PlayerColor.toUiColor(): Color = when (this) {
    PlayerColor.GREEN -> Color(0xFF20C060)
    PlayerColor.RED -> Color(0xFFE64545)
    PlayerColor.YELLOW -> Color(0xFFF2C230)
    PlayerColor.BLUE -> Color(0xFF2E7BEF)
}