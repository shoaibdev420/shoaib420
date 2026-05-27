package com.example.ludostealth.logic

/**
 * =========================================================
 * PATHS.KT
 * =========================================================
 *
 * YE FILE KYA KARTI HAI?
 * - Token movement ka road map rakhti hai
 * - Main circle path
 * - Har color ka start index
 * - Har color ki final home lane
 * - Safe star cells
 *
 * IMPORTANT:
 * - Yahan board image ka code nahi hota
 * - Yahan sirf movement ke cell coordinates hote hain
 *
 * BOARD SYSTEM:
 * - Ham board ko 15x15 grid samajh rahe hain
 * - row = upar se neeche
 * - col = left se right
 *
 * Example:
 * Cell(0, 0) = bilkul top-left
 * Cell(14, 14) = bilkul bottom-right
 *
 * FRACTION HELPER:
 * Agar baad me token render karna ho to cell ko fraction me convert kar sakte ho
 * taake image board ke upar place ho sake.
 */

enum class PlayerColor {
    GREEN, RED, YELLOW, BLUE
}

/**
 * Board ka ek cell
 *
 * row = horizontal line number (upar se neeche)
 * col = vertical line number (left se right)
 */
data class Cell(
    val row: Int,
    val col: Int
) {
    /**
     * Token ko board image ke upar place karne ke liye
     * cell center ki fractional position nikalta hai
     *
     * Example:
     * 0f..1f range me values milengi
     */
    fun toFraction(): Pair<Float, Float> {
        val x = (col + 0.5f) / 15f
        val y = (row + 0.5f) / 15f
        return x to y
    }
}

/**
 * =========================================================
 * MAIN OUTER PATH (52 CELLS)
 * =========================================================
 *
 * Ye pura outside circle path hai.
 * Hamne GREEN ke start point se path start kiya hai.
 *
 * IMPORTANT:
 * Agar future me lage ke token kisi cell par thoda off ja raha hai,
 * to board image ke hisaab se yahan cells adjust kiye ja sakte hain.
 */
object LudoPaths {

    val mainPath: List<Cell> = listOf(
        // GREEN side se start
        Cell(6, 1),
        Cell(6, 2),
        Cell(6, 3),
        Cell(6, 4),
        Cell(6, 5),

        Cell(5, 6),
        Cell(4, 6),
        Cell(3, 6),
        Cell(2, 6),
        Cell(1, 6),
        Cell(0, 6),

        Cell(0, 7),
        Cell(0, 8),

        // RED side
        Cell(1, 8),
        Cell(2, 8),
        Cell(3, 8),
        Cell(4, 8),
        Cell(5, 8),

        Cell(6, 9),
        Cell(6, 10),
        Cell(6, 11),
        Cell(6, 12),
        Cell(6, 13),
        Cell(6, 14),

        Cell(7, 14),
        Cell(8, 14),

        // BLUE side
        Cell(8, 13),
        Cell(8, 12),
        Cell(8, 11),
        Cell(8, 10),
        Cell(8, 9),

        Cell(9, 8),
        Cell(10, 8),
        Cell(11, 8),
        Cell(12, 8),
        Cell(13, 8),
        Cell(14, 8),

        Cell(14, 7),
        Cell(14, 6),

        // YELLOW side
        Cell(13, 6),
        Cell(12, 6),
        Cell(11, 6),
        Cell(10, 6),
        Cell(9, 6),

        Cell(8, 5),
        Cell(8, 4),
        Cell(8, 3),
        Cell(8, 2),
        Cell(8, 1),
        Cell(8, 0),

        Cell(7, 0),
        Cell(6, 0)
    )

    /**
     * =========================================================
     * START INDEX OF EACH COLOR ON MAIN PATH
     * =========================================================
     *
     * Ye batata hai home se nikalne ke baad token mainPath ke kis index par aayega.
     */
    val startIndexByColor: Map<PlayerColor, Int> = mapOf(
        PlayerColor.GREEN to 0,   // Cell(6,1)
        PlayerColor.RED to 13,    // Cell(1,8)
        PlayerColor.BLUE to 26,   // Cell(8,13)
        PlayerColor.YELLOW to 39  // Cell(13,6)
    )

    /**
     * =========================================================
     * FINAL HOME LANE OF EACH COLOR
     * =========================================================
     *
     * Token poora circle complete karne ke baad apni final lane me enter karega.
     * Ye wahi colored lane hai jo center ki taraf jati hai.
     */
    val homeLaneByColor: Map<PlayerColor, List<Cell>> = mapOf(
        PlayerColor.GREEN to listOf(
            Cell(7, 1),
            Cell(7, 2),
            Cell(7, 3),
            Cell(7, 4),
            Cell(7, 5),
            Cell(7, 6)
        ),
        PlayerColor.RED to listOf(
            Cell(1, 7),
            Cell(2, 7),
            Cell(3, 7),
            Cell(4, 7),
            Cell(5, 7),
            Cell(6, 7)
        ),
        PlayerColor.BLUE to listOf(
            Cell(7, 13),
            Cell(7, 12),
            Cell(7, 11),
            Cell(7, 10),
            Cell(7, 9),
            Cell(7, 8)
        ),
        PlayerColor.YELLOW to listOf(
            Cell(13, 7),
            Cell(12, 7),
            Cell(11, 7),
            Cell(10, 7),
            Cell(9, 7),
            Cell(8, 7)
        )
    )

    /**
     * =========================================================
     * SAFE STAR CELLS
     * =========================================================
     *
     * In cells par token capture nahi hoga.
     * Agar tumhare board image me stars thode different hon,
     * to yahan cells change kar sakte ho.
     */
    val safeCells: Set<Cell> = setOf(
        Cell(6, 1),   // green start star
        Cell(1, 8),   // red start star
        Cell(8, 13),  // blue start star
        Cell(13, 6),  // yellow start star

        Cell(8, 2),   // yellow extra star (left-bottom side)
        Cell(2, 6),   // green extra star (top-left side)
        Cell(6, 11),  // red extra star (top-right side)
        Cell(11, 8)   // blue extra star (bottom-right side)
    )

    /**
     * 2P / 3P / 4P ke mutabiq active colors
     *
     * 2P = RED + YELLOW
     * 3P = GREEN + RED + YELLOW
     * 4P = GREEN + RED + YELLOW + BLUE
     */
    fun activeColors(playersCount: Int): List<PlayerColor> {
        return when (playersCount) {
            2 -> listOf(PlayerColor.RED, PlayerColor.YELLOW)
            3 -> listOf(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.YELLOW)
            else -> listOf(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.YELLOW, PlayerColor.BLUE)
        }
    }

    /**
     * Kis color ka start cell kya hai
     */
    fun startCell(color: PlayerColor): Cell {
        val index = startIndexByColor[color]
            ?: error("Start index missing for color: $color")
        return mainPath[index]
    }

    /**
     * Kis color ki home lane kya hai
     */
    fun homeLane(color: PlayerColor): List<Cell> {
        return homeLaneByColor[color]
            ?: error("Home lane missing for color: $color")
    }

    /**
     * Kya ye cell safe star hai?
     */
    fun isSafeCell(cell: Cell): Boolean {
        return safeCells.contains(cell)
    }
}