package ca.ualberta.delbery.rapidrecall.model

/**
 * Read-only aggregate for the current in-memory session.
 *
 * Accuracy is exposed in both fractional and percentage form so the UI never duplicates the
 * empty-session rule.
 */
data class SessionSummary(
    val totalAttempts: Int,
    val correctAttempts: Int,
) {
    val accuracyFraction: Float
        get() = if (totalAttempts == 0) 0f else correctAttempts.toFloat() / totalAttempts

    val accuracyPercent: Int
        get() = (accuracyFraction * 100).toInt()
}
