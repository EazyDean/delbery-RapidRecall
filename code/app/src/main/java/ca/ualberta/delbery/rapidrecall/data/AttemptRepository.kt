package ca.ualberta.delbery.rapidrecall.data

import ca.ualberta.delbery.rapidrecall.model.Attempt

/**
 * Boundary around attempt storage. The interface keeps the game logic independent from the
 * assignment's session-only persistence choice and allows a database implementation later.
 */
interface AttemptRepository {
    fun add(attempt: Attempt)
    fun all(): List<Attempt>
}

/**
 * In-memory implementation used for this assignment. Records are private and callers receive a
 * snapshot, protecting the repository's internal collection from accidental modification.
 */
class SessionAttemptRepository : AttemptRepository {
    private val attempts = mutableListOf<Attempt>()

    override fun add(attempt: Attempt) {
        attempts += attempt
    }

    override fun all(): List<Attempt> = attempts.toList()
}
