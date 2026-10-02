package ca.ualberta.delbery.rapidrecall.domain

import kotlin.random.Random

/** Produces a digit string of the requested length for a new round. */
fun interface DigitSequenceGenerator {
    fun generate(length: Int): String
}

/**
 * Default random sequence generator. The digit supplier is injectable to make the rule easy to
 * test without relying on random outcomes.
 */
class RandomDigitSequenceGenerator(
    private val nextDigit: () -> Int = { Random.nextInt(from = 0, until = 10) },
) : DigitSequenceGenerator {
    override fun generate(length: Int): String {
        require(length in 1..10) { "Sequence length must be between 1 and 10." }
        return buildString(length) {
            repeat(length) {
                val digit = nextDigit()
                require(digit in 0..9) { "Digit supplier must return a value from 0 through 9." }
                append(digit)
            }
        }
    }
}
