package ca.ualberta.delbery.rapidrecall.domain

import ca.ualberta.delbery.rapidrecall.model.Attempt
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for session totals and accuracy calculation. */
class SummaryCalculatorTest {
    @Test
    fun calculate_countsAttemptsAndCorrectResults() {
        val attempts = listOf(
            attempt(correct = true),
            attempt(correct = false),
            attempt(correct = true),
        )

        val summary = SummaryCalculator.calculate(attempts)

        assertEquals(3, summary.totalAttempts)
        assertEquals(2, summary.correctAttempts)
        assertEquals(66, summary.accuracyPercent)
    }

    @Test
    fun calculate_emptySessionHasZeroAccuracy() {
        assertEquals(0, SummaryCalculator.calculate(emptyList()).accuracyPercent)
    }

    private fun attempt(correct: Boolean) = Attempt(
        sequenceLength = 2,
        userInput = if (correct) "12" else "21",
        targetSequence = "12",
        isCorrect = correct,
        timestampMillis = 1L,
    )
}
