package ca.ualberta.delbery.rapidrecall.domain

import ca.ualberta.delbery.rapidrecall.model.Attempt
import ca.ualberta.delbery.rapidrecall.model.SessionSummary

/** Converts attempt records into the statistics displayed on the home and summary screens. */
object SummaryCalculator {
    fun calculate(attempts: List<Attempt>): SessionSummary = SessionSummary(
        totalAttempts = attempts.size,
        correctAttempts = attempts.count(Attempt::isCorrect),
    )
}
