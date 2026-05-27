package com.example.ludostealth.logic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import kotlin.math.min

/**
 * =========================================================
 * CLEAN GLOSSY LUDO PAWN
 * =========================================================
 *
 * Is version me:
 * - Neeche ki ugly shadow remove kar di gayi hai
 * - Token ko clean glossy game-style look diya gaya hai
 * - Same shape har color ke liye rahegi
 * - Selected token ke liye simple white ring rakhi hai
 *
 * IMPORTANT:
 * - Bob animation + black rotating ring YAHAN nahi hogi
 * - Wo GameBoardScreen.kt me hogi
 *
 * EASY EDIT:
 * - Token overall size -> PawnToken(size = ...)
 * - Base size -> baseR
 * - Body size -> bodyR
 * - Head size -> headR
 * - Border thickness -> Stroke(width = ...)
 * - Shine intensity -> alpha values
 */
@Composable
fun PawnToken(
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {

            // =====================================================
            // BASIC SIZE HELPERS
            // =====================================================
            val w = this.size.width
            val h = this.size.height
            val s = min(w, h) // token ka real square size
            val c = Offset(w / 2f, h / 2f) // token ka center

            /**
             * Light source:
             * top-left se aa rahi hai
             * is se 3D/glossy look milega
             */
            val light = Offset(
                x = c.x - s * 0.22f,
                y = c.y - s * 0.24f
            )

            /**
             * Color mixing helper
             * color ko halka ya dark banane ke liye
             */
            fun mix(a: Color, b: Color, t: Float): Color {
                val tt = t.coerceIn(0f, 1f)
                return Color(
                    red = a.red + (b.red - a.red) * tt,
                    green = a.green + (b.green - a.green) * tt,
                    blue = a.blue + (b.blue - a.blue) * tt,
                    alpha = a.alpha + (b.alpha - a.alpha) * tt
                )
            }

            // =====================================================
            // COLOR SHADES
            // =====================================================
            val dark = mix(color, Color.Black, 0.22f)
            val darker = mix(color, Color.Black, 0.35f)
            val lightColor = mix(color, Color.White, 0.28f)
            val extraLight = mix(color, Color.White, 0.45f)

            // =====================================================
            // OPTIONAL SELECTED RING
            // =====================================================
            if (isSelected) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.90f),
                    radius = s * 0.49f,
                    center = c,
                    style = Stroke(width = s * 0.045f)
                )
            }

            // =====================================================
            // PAWN GEOMETRY
            // =====================================================
            /**
             * Yahan se token ka shape control hota hai
             *
             * Agar token ko mota/bara karna ho:
             * - baseR thora barhao
             * - bodyR thora barhao
             * - headR thora barhao
             */
            val baseR = s * 0.28f
            val bodyR = s * 0.21f
            val headR = s * 0.145f

            /**
             * Token ke 3 main parts ke center:
             * - base
             * - body
             * - head
             */
            val baseC = Offset(c.x, c.y + s * 0.20f)
            val bodyC = Offset(c.x, c.y + s * 0.02f)
            val headC = Offset(c.x, c.y - s * 0.17f)

            // =====================================================
            // BASE
            // =====================================================
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(extraLight, color, dark),
                    center = light,
                    radius = baseR * 1.9f
                ),
                radius = baseR,
                center = baseC
            )

            // Base border
            drawCircle(
                color = Color.Black.copy(alpha = 0.16f),
                radius = baseR,
                center = baseC,
                style = Stroke(width = s * 0.022f)
            )

            // Base top shine
            drawCircle(
                color = Color.White.copy(alpha = 0.14f),
                radius = baseR * 0.58f,
                center = Offset(
                    x = baseC.x - baseR * 0.18f,
                    y = baseC.y - baseR * 0.20f
                )
            )

            // =====================================================
            // BODY
            // =====================================================
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(extraLight, color, darker),
                    center = light,
                    radius = bodyR * 2.0f
                ),
                radius = bodyR,
                center = bodyC
            )

            // Body border
            drawCircle(
                color = Color.Black.copy(alpha = 0.14f),
                radius = bodyR,
                center = bodyC,
                style = Stroke(width = s * 0.020f)
            )

            /**
             * BODY SHINE STREAK
             * Is se token me glossy plastic jaisa look aata hai
             */
            val shinePath = Path().apply {
                moveTo(bodyC.x - bodyR * 0.52f, bodyC.y - bodyR * 0.10f)
                quadraticBezierTo(
                    bodyC.x - bodyR * 0.10f, bodyC.y - bodyR * 0.72f,
                    bodyC.x + bodyR * 0.38f, bodyC.y - bodyR * 0.16f
                )
                quadraticBezierTo(
                    bodyC.x + bodyR * 0.02f, bodyC.y - bodyR * 0.32f,
                    bodyC.x - bodyR * 0.52f, bodyC.y - bodyR * 0.10f
                )
                close()
            }

            drawPath(
                path = shinePath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    start = Offset(bodyC.x - bodyR, bodyC.y - bodyR),
                    end = Offset(bodyC.x + bodyR, bodyC.y + bodyR)
                )
            )

            // =====================================================
            // NECK
            // =====================================================
            val neckW = s * 0.18f
            val neckH = s * 0.10f
            val neckTopLeft = Offset(
                x = c.x - neckW / 2f,
                y = c.y - s * 0.08f
            )

            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(lightColor, color, dark),
                    start = neckTopLeft,
                    end = Offset(neckTopLeft.x + neckW, neckTopLeft.y + neckH)
                ),
                topLeft = neckTopLeft,
                size = Size(neckW, neckH),
                cornerRadius = CornerRadius(s * 0.05f, s * 0.05f)
            )

            drawRoundRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = neckTopLeft,
                size = Size(neckW, neckH),
                cornerRadius = CornerRadius(s * 0.05f, s * 0.05f),
                style = Stroke(width = s * 0.018f)
            )

            // =====================================================
            // HEAD
            // =====================================================
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(extraLight, color, darker),
                    center = light,
                    radius = headR * 2.0f
                ),
                radius = headR,
                center = headC
            )

            // Head border
            drawCircle(
                color = Color.Black.copy(alpha = 0.14f),
                radius = headR,
                center = headC,
                style = Stroke(width = s * 0.018f)
            )

            // Head highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.22f),
                radius = headR * 0.30f,
                center = Offset(
                    x = headC.x - headR * 0.22f,
                    y = headC.y - headR * 0.24f
                )
            )
        }
    }
}

/**
 * =========================================================
 * TOKEN IN CELL HELPER
 * =========================================================
 *
 * Agar tumhare paas sirf cellSize ho
 * to ye helper token ko automatically fit kar dega
 *
 * EASY EDIT:
 * 0.80f = normal size
 * 0.86f = thora bara
 * 0.74f = thora chhota
 */
@Composable
fun PawnTokenInCell(
    color: Color,
    cellSize: Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val tokenSize = cellSize * 0.90f
    PawnToken(
        color = color,
        size = tokenSize,
        modifier = modifier,
        isSelected = isSelected
    )
}

/**
 * =========================================================
 * READY LUDO COLORS
 * =========================================================
 */
object LudoColors {
    val Green = Color(0xFF1FAF3A)
    val Red = Color(0xFFEA2A2A)
    val Yellow = Color(0xFFF2B705)
    val Blue = Color(0xFF1E7BFF)
}