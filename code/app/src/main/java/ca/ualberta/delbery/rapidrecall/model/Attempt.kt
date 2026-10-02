package ca.ualberta.delbery.rapidrecall.model

/**
 * Immutable record of one completed recall attempt.
 *
 * Storing both the answer and target makes the log self-contained. [timestampMillis] uses a
 * platform-independent epoch value so formatting remains a presentation concern.
 */
data class Attempt(
    val sequenceLength: Int,
    val userInput: String,
    val targetSequence: String,
    val isCorrect: Boolean,
    val timestampMillis: Long,
)
