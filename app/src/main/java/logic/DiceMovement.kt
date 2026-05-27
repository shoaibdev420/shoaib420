package com.example.ludostealth.logic
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * =========================================================
 * PLAYER COLORS
 * =========================================================
 * In colors ko tum baad me turn, dice, token waghera me use kar sakte ho.
 */
enum class LudoPlayerColor {
    RED,
    GREEN,
    YELLOW,
    BLUE
}

/**
 * =========================================================
 * DICE RESULT
 * =========================================================
 * number    = final rolled number
 * extraTurn = agar 6 aaya ho
 */
data class DiceRollResult(
    val number: Int,
    val extraTurn: Boolean
)

/**
 * =========================================================
 * TURN ORDER
 * =========================================================
 * Red ko pehle rakhne ke liye standard order
 */
fun getTurnOrder(selectedPlayers: List<LudoPlayerColor>): List<LudoPlayerColor> {
    val defaultOrder = listOf(
        LudoPlayerColor.RED,
        LudoPlayerColor.GREEN,
        LudoPlayerColor.YELLOW,
        LudoPlayerColor.BLUE
    )
    return defaultOrder.filter { it in selectedPlayers }
}

/**
 * =========================================================
 * NEXT TURN HELPER
 * =========================================================
 * Rule:
 * - Agar 6 aaya to same player ki turn
 * - Warna next active player
 */

fun getNextTurn(
    currentTurn: LudoPlayerColor,
    activePlayers: List<LudoPlayerColor>,
    rolledNumber: Int
): LudoPlayerColor {
    if (activePlayers.isEmpty()) return currentTurn

    val turnOrder = getTurnOrder(activePlayers)
    val currentIndex = turnOrder.indexOf(currentTurn)

    if (currentIndex == -1) return turnOrder.first()

    val nextIndex = (currentIndex + 1) % turnOrder.size
    return turnOrder[nextIndex]
}

/**
 * =========================================================
 * DICE COLOR HELPER
 * =========================================================
 * Yahan se dice ke colors change kar sakte ho
 */
fun getDiceColor(player: LudoPlayerColor): Color {
    return when (player) {
        LudoPlayerColor.RED -> Color(0xFFE53935)
        LudoPlayerColor.GREEN -> Color(0xFF27C24C)
        LudoPlayerColor.YELLOW -> Color(0xFFF4C430)
        LudoPlayerColor.BLUE -> Color(0xFF2979FF)
    }
}

/**
 * =========================================================
 * MAIN DICE COMPOSABLE
 * =========================================================
 *
 * boxPlayer   = ye dice box kis color/player ka hai
 * currentTurn = abhi kis player ki turn hai
 * activePlayers = currently active players
 *
 * RULE:
 * - Sirf active turn wale player ke box me dice dikhna chahiye
 * - Dusre boxes me kuch show nahi hoga
 */
@Composable
fun DiceMovement(
    boxPlayer: LudoPlayerColor,
    currentTurn: LudoPlayerColor,
    activePlayers: List<LudoPlayerColor>,
    modifier: Modifier = Modifier,
    diceSize: Dp = 58.dp,
    cornerRadius: Dp = 14.dp,
    diceEnabled: Boolean = true ,
    onRollFinished: (DiceRollResult) -> Unit
) {
    // Agar ye current turn ka box nahi hai to dice show mat karo
    if (boxPlayer != currentTurn) return

    // Agar box player active players me nahi hai to bhi show mat karo
    if (boxPlayer !in activePlayers) return
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        DiceSound.init(context) }

    // Dice ka currently visible face number
    var currentFace by remember { mutableIntStateOf(1) }

    // Roll chal rahi hai ya nahi
    var isRolling by remember { mutableStateOf(false) }
    var rotationX by remember {mutableStateOf(0f)}
    var rotationY by remember {mutableStateOf(0f)}
    var scale by remember {mutableStateOf(1f)}

    /**
     * IMPORTANT:
     * In variable names ko alag rakha gaya hai
     * taake graphicsLayer ke rotationX/Y/Z se conflict na ho
     */
    val diceRotationZ = remember { Animatable(0f) }
    val diceRotationX = remember { Animatable(0f) }
    val diceRotationY = remember { Animatable(0f) }
    val diceScale = remember { Animatable(1f) }
    val diceOffsetX = remember { Animatable(0f) }
    val diceOffsetY = remember { Animatable(0f) }

    // Current player ke hisab se dice ka body color
    val diceBodyColor = getDiceColor(boxPlayer)

    Card(
        modifier = modifier
            .size(diceSize)
            .offset {
                IntOffset(
                    x = diceOffsetX.value.toInt(),
                    y = diceOffsetY.value.toInt()
                )
            }
            .graphicsLayer {
                /**
                 * Ye lines dice ko 3D jaisa roll feel deti hain
                 */
                rotationZ = diceRotationZ.value
                rotationX = diceRotationX.value
                rotationY = diceRotationY.value

                /**
                 * Scale se bounce / zoom effect milta hai
                 */
                scaleX = diceScale.value
                scaleY = diceScale.value

                /**
                 * Camera distance se thoda perspective milta hai
                 */
                cameraDistance = 12f * density
            }
            .clickable(enabled = !isRolling && diceEnabled) {
                if (isRolling) return@clickable
                DiceSound.play()
                isRolling = true
            },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = diceBodyColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(diceSize)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }

                .border(
                    width = 2.dp,
                    color = diceBodyColor.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            DiceDots(face = currentFace)
        }
    }

    /**
     * =========================================================
     * ROLL ANIMATION
     * =========================================================
     * User click karega -> dice ghoomega -> final number par rukega
     */
    LaunchedEffect(isRolling) {
        if (!isRolling) return@LaunchedEffect

        // Start se pehle sab reset
        diceRotationZ.snapTo(0f)
        diceRotationX.snapTo(0f)
        diceRotationY.snapTo(0f)
        diceScale.snapTo(1f)
        diceOffsetX.snapTo(0f)
        diceOffsetY.snapTo(0f)

        /**
         * Yahan roll animation chal rahi hai
         * repeat value badhaoge to roll lambi chalegi
         */
        repeat(3) { step ->
            // Roll ke duran random face show hota rahe
            currentFace = Random.nextInt(1, 7)
            rotationX += 180f
            rotationY += 180f
            scale = 1.1f

            // Random tilt values
            val randomX = listOf(-25f, -18f, -12f, 12f, 18f, 25f).random()
            val randomY = listOf(-25f, -18f, -12f, 12f, 18f, 25f).random()

            // Dice ko spin karao
            diceRotationZ.animateTo(
                targetValue = diceRotationZ.value + 180f,
                animationSpec = tween(
                    durationMillis = 45,
                    easing = LinearEasing
                )
            )

            // Thoda aage/peeche tilt
            diceRotationX.animateTo(
                targetValue = randomX,
                animationSpec = tween(
                    durationMillis = 45,
                    easing = FastOutSlowInEasing
                )
            )

            // Thoda left/right tilt
            diceRotationY.animateTo(
                targetValue = randomY,
                animationSpec = tween(
                    durationMillis = 45,
                    easing = FastOutSlowInEasing
                )
            )

            // Bounce / zoom effect
            diceScale.animateTo(
                targetValue = if (step % 2 == 0) 1.18f else 0.92f,
                animationSpec = tween(durationMillis = 35)
            )

            // Thoda move bhi kare
            diceOffsetX.animateTo(
                targetValue = listOf(-6f, -3f, 0f, 3f, 6f).random(),
                animationSpec = tween(durationMillis = 35)
            )

            diceOffsetY.animateTo(
                targetValue = listOf(-8f, -4f, 0f, 4f, 8f).random(),
                animationSpec = tween(durationMillis = 35)
            )

            delay(10)
            scale = 1f
        }

        // Final number
        val finalNumber = Random.nextInt(1, 7)
        currentFace = finalNumber

        // Final settle animation
        diceRotationX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
        )
        diceRotationY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
        )
        diceRotationZ.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
        )
        diceScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 60)
        )
        diceOffsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 60)
        )
        diceOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 60)
        )

        // Final me rotation reset
        diceRotationZ.snapTo(0f)

        isRolling = false

        // Result parent ko bhej do
        onRollFinished(
            DiceRollResult(
                number = finalNumber,
                extraTurn = finalNumber == 6
            )
        )
    }

}

/**
 * =========================================================
 * DICE DOTS
 * =========================================================
 * Face ke hisab se white dots show hoti hain
 */
@Composable
private fun BoxScope.DiceDots(face: Int) {
    when (face) {
        1 -> {
            DiceDot(Modifier.align(Alignment.Center))
        }

        2 -> {
            DiceDot(Modifier.align(Alignment.TopStart))
            DiceDot(Modifier.align(Alignment.BottomEnd))
        }

        3 -> {
            DiceDot(Modifier.align(Alignment.TopStart))
            DiceDot(Modifier.align(Alignment.Center))
            DiceDot(Modifier.align(Alignment.BottomEnd))
        }

        4 -> {
            DiceDot(Modifier.align(Alignment.TopStart))
            DiceDot(Modifier.align(Alignment.TopEnd))
            DiceDot(Modifier.align(Alignment.BottomStart))
            DiceDot(Modifier.align(Alignment.BottomEnd))
        }

        5 -> {
            DiceDot(Modifier.align(Alignment.TopStart))
            DiceDot(Modifier.align(Alignment.TopEnd))
            DiceDot(Modifier.align(Alignment.Center))
            DiceDot(Modifier.align(Alignment.BottomStart))
            DiceDot(Modifier.align(Alignment.BottomEnd))
        }

        6 -> {
            DiceDot(Modifier.align(Alignment.TopStart))
            DiceDot(Modifier.align(Alignment.CenterStart))
            DiceDot(Modifier.align(Alignment.BottomStart))

            DiceDot(Modifier.align(Alignment.TopEnd))
            DiceDot(Modifier.align(Alignment.CenterEnd))
            DiceDot(Modifier.align(Alignment.BottomEnd))
        }
    }
}

/**
 * =========================================================
 * SINGLE WHITE DOT
 * =========================================================
 * Yahan se dot ka size change kar sakte ho
 */
@Composable
private fun DiceDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )
}
