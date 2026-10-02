package ca.ualberta.delbery.rapidrecall.ui

import ca.ualberta.delbery.rapidrecall.data.SessionAttemptRepository
import ca.ualberta.delbery.rapidrecall.domain.DigitSequenceGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the complete round state transition and logging behavior. */
class RapidRecallViewModelTest {
    @Test
    fun correctRound_isLoggedAndIncludedInSummary() {
        val repository = SessionAttemptRepository()
        val viewModel = RapidRecallViewModel(
            sequenceGenerator = DigitSequenceGenerator { "1234" },
            repository = repository,
        )

        viewModel.openGame()
        viewModel.beginSequence()
        viewModel.finishPlayback()
        viewModel.updateGuess("1234")
        viewModel.submitGuess(nowMillis = 99L)

        assertEquals(GamePhase.FEEDBACK, viewModel.phase)
        assertTrue(requireNotNull(viewModel.feedback).isCorrect)
        assertEquals(1, viewModel.summary.totalAttempts)
        assertEquals(100, viewModel.summary.accuracyPercent)
    }

    @Test
    fun incompleteGuess_isNotSubmitted() {
        val viewModel = RapidRecallViewModel(
            sequenceGenerator = DigitSequenceGenerator { "1234" },
            repository = SessionAttemptRepository(),
        )

        viewModel.openGame()
        viewModel.beginSequence()
        viewModel.finishPlayback()
        viewModel.updateGuess("12")
        viewModel.submitGuess(nowMillis = 99L)

        assertEquals(GamePhase.INPUT, viewModel.phase)
        assertFalse(viewModel.attempts.isNotEmpty())
    }
}
