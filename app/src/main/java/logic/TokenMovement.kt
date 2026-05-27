package com.example.ludostealth.logic

/**
 * =========================================================
 * TOKEN MOVEMENT.KT
 * =========================================================
 *
 * YE FILE KYA KARTI HAI?
 * - Token ko kis kis cell se move karna hai, wo calculate karti hai
 * - Dice value ke hisaab se step-by-step path list banati hai
 * - UI baad me isi list ko use karke animation dikhayegi
 *
 * IMPORTANT:
 * - Is file me board draw nahi hota
 * - Is file me dice random roll nahi hota
 * - Is file me UI animation directly nahi hoti
 *
 * YE FILE SIRF MOVEMENT CALCULATION ke liye hai
 *
 * EASY CHANGE:
 * - Agar future me movement rules change karne hon
 * - Agar path indexing me issue ho
 * - Agar home lane entry rule change karna ho
 * To yahan comments ke sath asani se kar sakte ho
 */

/**
 * =========================================================
 * TOKEN STATE
 * =========================================================
 *
 * Har token ki current position ko samajhne ke liye ye data class hai.
 *
 * isAtHome:
 *   true  = token abhi home me hai
 *   false = token board ya home lane me hai
 *
 * isFinished:
 *   true  = token finish ho chuka hai
 *
 * mainPathIndex:
 *   agar token main outer path par hai to uska current index
 *
 * homeLaneIndex:
 *   agar token apni final home lane me aa gaya hai to uska index
 *
 * stepsMoved:
 *   token ne total kitne steps move kiye hain
 *   isko use karke hum check karenge ke full circle complete hua ya nahi
 */
data class TokenMovementState(
    val color: PlayerColor,
    val isAtHome: Boolean = true,
    val isFinished: Boolean = false,
    val mainPathIndex: Int? = null,
    val homeLaneIndex: Int? = null,
    val stepsMoved: Int = 0
)

/**
 * =========================================================
 * MOVE RESULT
 * =========================================================
 *
 * stepCells:
 *   token kis kis cell se guzrega (step by step)
 *
 * newState:
 *   move ke baad token ki updated state
 *
 * canMove:
 *   token move valid tha ya nahi
 *
 * reason:
 *   agar move invalid ho to kyun invalid tha
 */
data class TokenMoveResult(
    val stepCells: List<Cell>,
    val newState: TokenMovementState,
    val canMove: Boolean,
    val reason: String? = null
)

/**
 * =========================================================
 * TOKEN MOVEMENT HELPER
 * =========================================================
 */
object TokenMovementHelper {

    /**
     * Har token ko total 51 outer steps + 6 home lane steps chalne hote hain.
     *
     * Simple logic:
     * - start cell par aane ke baad token mainPath par move karega
     * - full circle ke baad apni home lane me enter karega
     * - exact steps par finish karega
     */
    private const val MAIN_PATH_SIZE = 52
    private const val HOME_LANE_SIZE = 6

    /**
     * =========================================================
     * KYA TOKEN MOVE HO SAKTA HAI?
     * =========================================================
     *
     * Rule:
     * - token home me hai to sirf 6 par nikal sakta hai
     * - finished token move nahi karega
     * - token ko exact finish chahiye
     */
    fun canTokenMove(
        token: TokenMovementState,
        diceValue: Int
    ): Boolean {
        // invalid dice safety
        if (diceValue !in 1..6) return false

        // finished token move nahi karega
        if (token.isFinished) return false

        // home me hai to sirf 6 par niklega
        if (token.isAtHome) {
            return diceValue == 6
        }

        // total path length:
        // 51 steps outer travel + 6 home lane = 57
        val totalAllowedSteps = 57

        // exact finish rule:
        // agar dice zyada hai aur exact finish cross ho rahi hai to move invalid
        val futureSteps = token.stepsMoved + diceValue
        return futureSteps <= totalAllowedSteps
    }

    /**
     * =========================================================
     * TOKEN MOVE BUILD KARO
     * =========================================================
     *
     * Ye main function hai:
     * - move valid hai ya nahi check karega
     * - step by step cells list banayega
     * - new token state return karega
     */
    fun buildMove(
        token: TokenMovementState,
        diceValue: Int
    ): TokenMoveResult {

        // Pehle check karo token move kar sakta hai ya nahi
        if (!canTokenMove(token, diceValue)) {
            return TokenMoveResult(
                stepCells = emptyList(),
                newState = token,
                canMove = false,
                reason = "Token move allowed nahi hai"
            )
        }

        // -----------------------------------------------------
        // CASE 1: Token abhi home me hai
        // Rule: sirf 6 par home se nikalta hai
        // Aur apne start cell par aata hai
        // -----------------------------------------------------
        if (token.isAtHome && diceValue == 6) {
            val startIndex = LudoPaths.startIndexByColor[token.color]
                ?: error("Start index missing for ${token.color}")

            val startCell = LudoPaths.mainPath[startIndex]

            val newState = token.copy(
                isAtHome = false,
                isFinished = false,
                mainPathIndex = startIndex,
                homeLaneIndex = null,
                stepsMoved = 0
            )

            return TokenMoveResult(
                stepCells = listOf(startCell),
                newState = newState,
                canMove = true
            )
        }

        // -----------------------------------------------------
        // CASE 2: Token already board par hai
        // Step by step cells build karo
        // -----------------------------------------------------
        val stepCells = mutableListOf<Cell>()

        var currentMainPathIndex = token.mainPathIndex
        var currentHomeLaneIndex = token.homeLaneIndex
        var currentStepsMoved = token.stepsMoved

        repeat(diceValue) {
            val next = nextStep(
                color = token.color,
                currentMainPathIndex = currentMainPathIndex,
                currentHomeLaneIndex = currentHomeLaneIndex,
                currentStepsMoved = currentStepsMoved
            )

            currentMainPathIndex = next.mainPathIndex
            currentHomeLaneIndex = next.homeLaneIndex
            currentStepsMoved += 1

            stepCells.add(next.cell)
        }

        // move ke baad check karo token finish hua ya nahi
        val isNowFinished = currentStepsMoved == 57

        val newState = TokenMovementState(
            color = token.color,
            isAtHome = false,
            isFinished = isNowFinished,
            mainPathIndex = if (isNowFinished) null else currentMainPathIndex,
            homeLaneIndex = if (isNowFinished) null else currentHomeLaneIndex,
            stepsMoved = currentStepsMoved
        )

        return TokenMoveResult(
            stepCells = stepCells,
            newState = newState,
            canMove = true
        )
    }

    /**
     * =========================================================
     * NEXT SINGLE STEP
     * =========================================================
     *
     * Ye function token ka next 1 step nikalta hai.
     * Dice 5 hai to buildMove() isko 5 dafa call karega.
     *
     * Rules:
     * - Pehle main outer path
     * - Full circle complete karne ke baad home lane
     */
    private fun nextStep(
        color: PlayerColor,
        currentMainPathIndex: Int?,
        currentHomeLaneIndex: Int?,
        currentStepsMoved: Int
    ): SingleStepResult {

        // Agar token abhi home lane me aa chuka hai
        if (currentHomeLaneIndex != null) {
            val nextHomeLaneIndex = currentHomeLaneIndex + 1
            val homeLane = LudoPaths.homeLane(color)
            return SingleStepResult(
                cell = homeLane[nextHomeLaneIndex],
                mainPathIndex = null,
                homeLaneIndex = nextHomeLaneIndex
            )
        }

        // Agar token main outer path par hai
        if (currentMainPathIndex != null) {

            // Jab token 51 steps outer path par chal chuka ho
            // to next step home lane me enter karega
            if (currentStepsMoved >= 51) {
                val homeLane = LudoPaths.homeLane(color)
                return SingleStepResult(
                    cell = homeLane[0],
                    mainPathIndex = null,
                    homeLaneIndex = 0
                )
            }

            // Normal outer path next step
            val nextIndex = (currentMainPathIndex + 1) % MAIN_PATH_SIZE
            return SingleStepResult(
                cell = LudoPaths.mainPath[nextIndex],
                mainPathIndex = nextIndex,
                homeLaneIndex = null
            )
        }

        error("Invalid token state: na mainPathIndex hai na homeLaneIndex")
    }

    /**
     * =========================================================
     * KYA YE CELL SAFE HAI?
     * =========================================================
     *
     * Capture logic me use hoga.
     */
    fun isSafeCell(cell: Cell): Boolean {
        return LudoPaths.isSafeCell(cell)
    }
}

/**
 * Single next step helper result
 */
private data class SingleStepResult(
    val cell: Cell,
    val mainPathIndex: Int?,
    val homeLaneIndex: Int?
)