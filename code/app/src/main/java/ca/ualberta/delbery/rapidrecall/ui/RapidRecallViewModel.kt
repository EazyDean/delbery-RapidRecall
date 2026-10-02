package ca.ualberta.delbery.rapidrecall.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ca.ualberta.delbery.rapidrecall.data.AttemptRepository
import ca.ualberta.delbery.rapidrecall.data.SessionAttemptRepository
import ca.ualberta.delbery.rapidrecall.domain.DigitSequenceGenerator
import ca.ualberta.delbery.rapidrecall.domain.RandomDigitSequenceGenerator
import ca.ualberta.delbery.rapidrecall.domain.SummaryCalculator
import ca.ualberta.delbery.rapidrecall.model.Attempt
import ca.ualberta.delbery.rapidrecall.model.SessionSummary

/** Top-level destinations in the single-activity application. */
enum class AppScreen { HOME, GAME, LOG, SUMMARY }

/** Distinct phases of a round; invalid actions are ignored outside their expected phase. */
enum class GamePhase { READY, MEMORIZE, INPUT, FEEDBACK }

/**
 * Coordinates navigation, round state, validation, and session logging.
 *
 * Dependencies are expressed as interfaces/default collaborators to keep rules testable. Compose
 * observes the exposed state, while mutation remains private to this class.
 */
class RapidRecallViewModel(
    private val sequenceGenerator: DigitSequenceGenerator = RandomDigitSequenceGenerator(),
    private val repository: AttemptRepository = SessionAttemptRepository(),
) : ViewModel() {
    var screen by mutableStateOf(AppScreen.HOME)
        private set
    var phase by mutableStateOf(GamePhase.READY)
        private set
    var selectedLength by mutableIntStateOf(4)
        private set
    var activeDigit by mutableStateOf<Int?>(null)
        private set
    var digitPosition by mutableIntStateOf(0)
        private set
    var enteredGuess by mutableStateOf("")
        private set
    var feedback by mutableStateOf<Attempt?>(null)
        private set
    var playbackId by mutableIntStateOf(0)
        private set

    private var targetSequence = ""

    val attempts: List<Attempt>
        get() = repository.all()

    val summary: SessionSummary
        get() = SummaryCalculator.calculate(repository.all())

    fun openGame() {
        resetRound()
        screen = AppScreen.GAME
    }

    fun openLog() {
        screen = AppScreen.LOG
    }

    fun openSummary() {
        screen = AppScreen.SUMMARY
    }

    fun goHome() {
        activeDigit = null
        screen = AppScreen.HOME
    }

    fun selectLength(length: Int) {
        if (phase == GamePhase.READY && length in 1..10) selectedLength = length
    }

    fun beginSequence() {
        if (phase != GamePhase.READY) return
        targetSequence = sequenceGenerator.generate(selectedLength)
        enteredGuess = ""
        feedback = null
        activeDigit = null
        digitPosition = 0
        phase = GamePhase.MEMORIZE
        playbackId += 1
    }

    fun revealDigit(index: Int) {
        if (phase != GamePhase.MEMORIZE || index !in targetSequence.indices) return
        activeDigit = targetSequence[index].digitToInt()
        digitPosition = index + 1
    }

    fun hideDigit() {
        if (phase == GamePhase.MEMORIZE) activeDigit = null
    }

    fun finishPlayback() {
        if (phase == GamePhase.MEMORIZE) {
            activeDigit = null
            phase = GamePhase.INPUT
        }
    }

    fun updateGuess(rawValue: String) {
        if (phase != GamePhase.INPUT) return
        enteredGuess = rawValue.filter(Char::isDigit).take(selectedLength)
    }

    fun submitGuess(nowMillis: Long = System.currentTimeMillis()) {
        if (phase != GamePhase.INPUT || enteredGuess.length != selectedLength) return
        val attempt = Attempt(
            sequenceLength = selectedLength,
            userInput = enteredGuess,
            targetSequence = targetSequence,
            isCorrect = enteredGuess == targetSequence,
            timestampMillis = nowMillis,
        )
        repository.add(attempt)
        feedback = attempt
        phase = GamePhase.FEEDBACK
    }

    fun playAgain() {
        if (phase == GamePhase.FEEDBACK) resetRound()
    }

    private fun resetRound() {
        phase = GamePhase.READY
        activeDigit = null
        digitPosition = 0
        enteredGuess = ""
        targetSequence = ""
        feedback = null
    }
}
