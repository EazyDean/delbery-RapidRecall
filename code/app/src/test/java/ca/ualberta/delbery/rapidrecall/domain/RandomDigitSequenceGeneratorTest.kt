package ca.ualberta.delbery.rapidrecall.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** Unit tests for sequence length, order, and input boundary rules. */
class RandomDigitSequenceGeneratorTest {
    @Test
    fun generate_returnsRequestedNumberOfDigits() {
        var digit = 0
        val generator = RandomDigitSequenceGenerator { (digit++ % 10) }

        assertEquals("012345", generator.generate(6))
    }

    @Test
    fun generate_rejectsLengthOutsideAssignmentRange() {
        val generator = RandomDigitSequenceGenerator { 4 }

        assertThrows(IllegalArgumentException::class.java) { generator.generate(0) }
        assertThrows(IllegalArgumentException::class.java) { generator.generate(11) }
    }
}
