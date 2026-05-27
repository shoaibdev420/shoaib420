package com.example.ludostealth.logic

/**
 * =========================================================
 * GAME LOGIC
 * =========================================================
 *
 * YE FILE KYA KARTI HAI?
 * - Dice ka final number receive karti hai
 * - Current turn ka logic chalati hai
 * - Kaunse token move ho sakte hain ye batati hai
 * - Token move apply karti hai
 * - Safe star par kill nahi hone deti
 * - Opponent token ko home bhejti hai
 * - Winner decide karti hai
 *
 * IMPORTANT:
 * - Dice animation yahan nahi hoti
 * - DiceMovement.kt sirf animation + final number deta hai
 * - TokenMovement.kt step-by-step movement path banata hai
 * - Paths.kt board ka road map rakhti hai
 */

/* =========================================================
 * GAME TOKEN
 * ========================================================= */
data class LogicToken(
    val id: Int,
    val movement: TokenMovementState
)

/* =========================================================
 * GAME STATE
 * ========================================================= */
data class LogicGameState(
    val playersCount: Int,
    val activePlayers: List<LudoPlayerColor>,
    val currentTurn: LudoPlayerColor,
    val lastDiceNumber: Int? = null,
    val selectableTokenIds: List<Int> = emptyList(),
    val tokens: List<LogicToken>,
    val winner: LudoPlayerColor? = null
)

/* =========================================================
 * TOKEN MOVE RESULT FOR UI
 * ========================================================= */
data class GameMoveResult(
    val newState: LogicGameState,
    val movedPath: List<Cell>,
    val message: String? = null
)

/* =========================================================
 * MAIN GAME LOGIC OBJECT
 * ========================================================= */
object GameLogic {

    /**
     * =====================================================
     * INITIAL GAME STATE
     * =====================================================
     *
     * 2P = RED + YELLOW
     * 3P = GREEN + RED + YELLOW
     * 4P = GREEN + RED + YELLOW + BLUE
     */
    fun createInitialState(playersCount: Int): LogicGameState {
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

        val tokens = activePlayers.flatMap { color ->
            listOf(
                LogicToken(id = tokenId(color, 1), movement = TokenMovementState(color = color.toPathColor())),
                LogicToken(id = tokenId(color, 2), movement = TokenMovementState(color = color.toPathColor())),
                LogicToken(id = tokenId(color, 3), movement = TokenMovementState(color = color.toPathColor())),
                LogicToken(id = tokenId(color, 4), movement = TokenMovementState(color = color.toPathColor()))
            )
        }

        return LogicGameState(
            playersCount = playersCount,
            activePlayers = activePlayers,
            currentTurn = activePlayers.first(),
            lastDiceNumber = null,
            selectableTokenIds = emptyList(),
            tokens = tokens,
            winner = null
        )
    }

    /**
     * =====================================================
     * DICE FINAL NUMBER RECEIVE
     * =====================================================
     *
     * Ye function DiceMovement.kt se aane wale FINAL number ko handle karega.
     *
     * RULE:
     * - Agar koi token move ho sakta hai, to user token tap karega
     * - Agar koi token move nahi ho sakta:
     *      - 6 ho to same player ko dobara turn
     *      - warna next player
     */
    fun onDiceRolled(
        state: LogicGameState,
        rolledNumber: Int
    ): LogicGameState {
        if (state.lastDiceNumber != null) {
            return state
        }
        if (state.winner != null) return state

        val movableTokenIds = state.tokens
            .filter { it.movement.color.toLudoColor() == state.currentTurn }
            .filter { TokenMovementHelper.canTokenMove(it.movement, rolledNumber) }
            .map { it.id }

        // Agar koi token move nahi ho sakta
        // Agar koi token move nahi ho sakta to turn khatam
        if (movableTokenIds.isEmpty()) {
          val nextTurn = getNextTurn(
              currentTurn = state.currentTurn,
              activePlayers = state.activePlayers,
              rolledNumber = rolledNumber
          )

            return state.copy(
                currentTurn = nextTurn,
                lastDiceNumber = null,
                selectableTokenIds = emptyList()
            )
        }

        // Agar move possible hai to token select karna hoga
        return state.copy(
            lastDiceNumber = rolledNumber,
            selectableTokenIds = movableTokenIds
        )
    }

    /**
     * =====================================================
     * TOKEN CLICK / APPLY MOVE
     * =====================================================
     *
     * User token tap karega -> token move hoga
     */
    fun onTokenSelected(
        state: LogicGameState,
        tokenId: Int
    ): GameMoveResult {
        if (state.winner != null) {
            return GameMoveResult(
                newState = state,
                movedPath = emptyList(),
                message = "Game already finished"
            )
        }

        val diceNumber = state.lastDiceNumber
            ?: return GameMoveResult(
                newState = state,
                movedPath = emptyList(),
                message = "Pehle dice roll karo"
            )

        if (!state.selectableTokenIds.contains(tokenId)) {
            return GameMoveResult(
                newState = state,
                movedPath = emptyList(),
                message = "Ye token move nahi ho sakta"
            )
        }

        val oldToken = state.tokens.find { it.id == tokenId }
            ?: return GameMoveResult(
                newState = state,
                movedPath = emptyList(),
                message = "Token nahi mila"
            )

        val moveResult = TokenMovementHelper.buildMove(
            token = oldToken.movement,
            diceValue = diceNumber
        )

        if (!moveResult.canMove) {
            return GameMoveResult(
                newState = state,
                movedPath = emptyList(),
                message = moveResult.reason ?: "Move allowed nahi"
            )
        }

        // 1) Selected token update karo
        var updatedTokens = state.tokens.map { token ->
            if (token.id == tokenId) {
                token.copy(movement = moveResult.newState)
            } else {
                token
            }
        }

        // 2) Final landed cell nikalo
        val landedCell = moveResult.stepCells.lastOrNull()
        var didCapture = false

        // 3) Capture logic
        if (landedCell != null && !TokenMovementHelper.isSafeCell(landedCell)) {
            updatedTokens = updatedTokens.map { other ->
                if (other.id == tokenId) {
                    other
                } else {
                    val otherCell = currentCellOf(other.movement)

                    // same color ko kill nahi karna
                    val isSameColor = other.movement.color == moveResult.newState.color

                    // home/finish token ko kill nahi karna
                    val canBeKilled = !other.movement.isAtHome && !other.movement.isFinished

                    if (!isSameColor && canBeKilled && otherCell == landedCell) {
                        didCapture = true
                        // token ko wapas home bhej do
                        other.copy(
                            movement = TokenMovementState(
                                color = other.movement.color
                            )
                        )
                    } else {
                        other
                    }
                }
            }
        }

        // 4) Winner check
        val winner = findWinner(updatedTokens)

        // 5) Next turn / extra turn
        val nextTurn = if (winner != null) {
            state.currentTurn
        } else if (diceNumber == 6 || didCapture) {
            // 6 par same player ko extra turn
            state.currentTurn
        } else {
            getNextTurn(
                currentTurn = state.currentTurn,
                activePlayers = state.activePlayers,
                rolledNumber = diceNumber
            )
        }

        val newState = state.copy(
            currentTurn = nextTurn,
            lastDiceNumber = null,
            selectableTokenIds = emptyList(),
            tokens = updatedTokens,
            winner = winner
        )

        return GameMoveResult(
            newState = newState,
            movedPath = moveResult.stepCells,
            message = if (winner != null) "${winner.name} wins!" else null
        )
    }

    /**
     * =====================================================
     * CURRENT CELL OF TOKEN
     * =====================================================
     *
     * UI ko batata hai token abhi kis cell par hai
     */
    fun currentCellOf(movement: TokenMovementState): Cell? {
        if (movement.isAtHome) return null
        if (movement.isFinished) return null

        if (movement.homeLaneIndex != null) {
            return LudoPaths.homeLane(movement.color)[movement.homeLaneIndex]
        }

        if (movement.mainPathIndex != null) {
            return LudoPaths.mainPath[movement.mainPathIndex]
        }

        return null
    }

    /**
     * =====================================================
     * WINNER CHECK
     * =====================================================
     *
     * 4 tokens finish = winner
     */
    private fun findWinner(tokens: List<LogicToken>): LudoPlayerColor? {
        val grouped = tokens.groupBy { it.movement.color.toLudoColor() }

        grouped.forEach { (color, tokenList) ->
            val finishedCount = tokenList.count { it.movement.isFinished }
            if (finishedCount == 4) return color
        }

        return null
    }

    /**
     * =====================================================
     * TOKEN ID
     * =====================================================
     *
     * RED    = 200 series
     * GREEN  = 100 series
     * YELLOW = 300 series
     * BLUE   = 400 series
     *
     * Is se slot number nikalna easy rehta hai
     */
    private fun tokenId(color: LudoPlayerColor, number: Int): Int {
        return when (color) {
            LudoPlayerColor.GREEN -> 100 + number
            LudoPlayerColor.RED -> 200 + number
            LudoPlayerColor.YELLOW -> 300 + number
            LudoPlayerColor.BLUE -> 400 + number
        }
    }
}

/* =========================================================
 * COLOR MAPPERS
 * ========================================================= */
private fun LudoPlayerColor.toPathColor(): PlayerColor = when (this) {
    LudoPlayerColor.GREEN -> PlayerColor.GREEN
    LudoPlayerColor.RED -> PlayerColor.RED
    LudoPlayerColor.YELLOW -> PlayerColor.YELLOW
    LudoPlayerColor.BLUE -> PlayerColor.BLUE
}

private fun PlayerColor.toLudoColor(): LudoPlayerColor = when (this) {
    PlayerColor.GREEN -> LudoPlayerColor.GREEN
    PlayerColor.RED -> LudoPlayerColor.RED
    PlayerColor.YELLOW -> LudoPlayerColor.YELLOW
    PlayerColor.BLUE -> LudoPlayerColor.BLUE
}