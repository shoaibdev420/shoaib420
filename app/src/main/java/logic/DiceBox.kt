package com.example.ludostealth.logic
import androidx.compose.foundation.clickable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * =========================================================
 * DICE BOX OVERLAY
 * =========================================================
 * - Abhi box andar se empty hai
 * - Har color ka alag border / glow hai
 * - Sirf active players ke boxes show honge
 *
 * IMPORTANT:
 * Ye boxes BOARD ke relative place honge.
 * Isliye board jahan move hoga, boxes bhi uske hisaab se rahenge.
 *
 * EASY EDIT:
 * 1) box ka size -> boxSize
 * 2) border color -> toDiceBorderColor()
 * 3) glow / background -> DiceSingleBox()
 * 4) positions -> DiceBoxPos object
 *
 * POSITION RULE:
 * x value:
 *   chhota = left
 *   bara   = right
 *
 * y value:
 *   chhota = upar
 *   bara   = neeche
 */

private enum class DiceColor {
    GREEN, RED, YELLOW, BLUE
}
private fun DiceColor.toLudoPlayerColor(): LudoPlayerColor = when (this) {
    DiceColor.GREEN -> LudoPlayerColor.GREEN
    DiceColor.RED -> LudoPlayerColor.RED
    DiceColor.YELLOW -> LudoPlayerColor.YELLOW
    DiceColor.BLUE -> LudoPlayerColor.BLUE
}

private fun DiceColor.toDiceBorderColor(): Color = when (this) {
    DiceColor.GREEN -> Color(0xFF27D34D)
    DiceColor.RED -> Color(0xFFFF4A4A)
    DiceColor.YELLOW -> Color(0xFFFFC83D)
    DiceColor.BLUE -> Color(0xFF2E7BEF)
}

private fun DiceColor.toInnerGlowColor(): Color = when (this) {
    DiceColor.GREEN -> Color(0xFF183E1E)
    DiceColor.RED -> Color(0xFF3E1818)
    DiceColor.YELLOW -> Color(0xFF4A3A14)
    DiceColor.BLUE -> Color(0xFF182C4A)
}

/**
 * =========================================================
 * DICE BOX POSITIONS
 * =========================================================
 * Ye values BOARD ke relative fractions hain.
 *
 * Green  = left-top side
 * Red    = right-top side
 * Yellow = left-bottom side
 * Blue   = right-bottom side
 *
 * Agar box ko move karna ho:
 *
 * X:
 *  chhota = left
 *  bara   = right
 *
 * Y:
 *  chhota = upar
 *  bara   = neeche
 */
private object DiceBoxPos {

    // GREEN box (board ke left-top side)
    val green = 0.19f to -0.08f

    // RED box (board ke right-top side)
    val red = 0.81f to -0.08f

    // YELLOW box (board ke left-bottom side)
    val yellow = 0.19f to 1.08f

    // BLUE box (board ke right-bottom side)
    val blue = 0.81f to 1.08f
}

/**
 * =========================================================
 * SINGLE DICE BOX UI
 * =========================================================
 * Abhi empty box hai.
 * Baad me isi ke andar dice add karenge.
 */
@Composable
private fun DiceSingleBox(
    color: DiceColor,
    currentTurn: LudoPlayerColor,
    activePlayers: List<LudoPlayerColor>,
    diceEnabled: Boolean,
    onDiceRolled: (DiceRollResult) -> Unit,
    modifier: Modifier = Modifier
) {
    // BOX SIZE:
    // 54.dp = chhota
    // 60.dp = medium
    // 66.dp = bara
    val boxSize = 58.dp

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF302D67),
                        Color(0xFF24214F)
                    )
                )
            )
            .border(
                width = 4.dp,
                color = color.toDiceBorderColor(),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        // INNER EMPTY AREA
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            color.toInnerGlowColor().copy(alpha = 0.75f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            DiceMovement(
                boxPlayer = color.toLudoPlayerColor(),
                currentTurn = currentTurn,
                activePlayers = activePlayers,
                diceSize = 36.dp, // BOX ke andar fit karne ke liye chhota dice
                cornerRadius = 10.dp,
                diceEnabled = diceEnabled,
                onRollFinished = { result ->
                    onDiceRolled(result)
                }
            )
        }
    }
}


/**
 * =========================================================
 * DICE BOXES OVERLAY
 * =========================================================
 * Is function ko board ke upar/around call karna hai.
 *
 * playersCount:
 * 2P = RED + YELLOW
 * 3P = GREEN + RED + YELLOW
 * 4P = GREEN + RED + YELLOW + BLUE
 */
@Composable
fun BoxScope.DiceBoxesOverlay(
    playersCount: Int,
    currentTurn: LudoPlayerColor,
    diceEnabled: Boolean,
    onDiceRolled: (DiceRollResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlayers = when (playersCount) {
        2 -> listOf(LudoPlayerColor.RED, LudoPlayerColor.YELLOW)
        3 -> listOf(LudoPlayerColor.GREEN, LudoPlayerColor.RED, LudoPlayerColor.YELLOW)
        else -> listOf(
            LudoPlayerColor.GREEN,
            LudoPlayerColor.RED,
            LudoPlayerColor.YELLOW,
            LudoPlayerColor.BLUE
        )
    }

    val activeColors = when (playersCount) {
        2 -> setOf(DiceColor.RED, DiceColor.YELLOW)
        3 -> setOf(DiceColor.GREEN, DiceColor.RED, DiceColor.YELLOW)
        else -> setOf(DiceColor.GREEN, DiceColor.RED, DiceColor.YELLOW, DiceColor.BLUE)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val w = maxWidth
        val h = maxHeight

        @Composable
        fun placeDiceBox(
            xFrac: Float,
            yFrac: Float,
            color: DiceColor
        ) {
            DiceSingleBox(
                color = color,
                currentTurn = currentTurn,
                activePlayers = activePlayers,
                diceEnabled = diceEnabled,
                onDiceRolled = onDiceRolled,
                modifier = Modifier.offset(
                    x = (w * xFrac) - 29.dp, // 29.dp = half of 58.dp
                    y = (h * yFrac) - 29.dp
                )
            )
        }

        if (activeColors.contains(DiceColor.GREEN)) {
            placeDiceBox(DiceBoxPos.green.first, DiceBoxPos.green.second, DiceColor.GREEN)
        }

        if (activeColors.contains(DiceColor.RED)) {
            placeDiceBox(DiceBoxPos.red.first, DiceBoxPos.red.second, DiceColor.RED)
        }

        if (activeColors.contains(DiceColor.YELLOW)) {
            placeDiceBox(DiceBoxPos.yellow.first, DiceBoxPos.yellow.second, DiceColor.YELLOW)
        }

        if (activeColors.contains(DiceColor.BLUE)) {
            placeDiceBox(DiceBoxPos.blue.first, DiceBoxPos.blue.second, DiceColor.BLUE)
        }
    }
}