package ca.ualberta.delbery.rapidrecall.data

import ca.ualberta.delbery.rapidrecall.model.Attempt
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit test confirming session records preserve completion order. */
class SessionAttemptRepositoryTest {
    @Test
    fun add_retainsAttemptsInCompletionOrder() {
        val repository = SessionAttemptRepository()
        val first = attempt("1")
        val second = attempt("2")

        repository.add(first)
        repository.add(second)

        assertEquals(listOf(first, second), repository.all())
    }

    private fun attempt(value: String) = Attempt(
        sequenceLength = 1,
        userInput = value,
        targetSequence = value,
        isCorrect = true,
        timestampMillis = 1L,
    )
}
