package ru.radixit.letsplay

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun isActualList() {
        assertEquals(3, MaxNumber().maxNumber(listOf(3, 3, 3, 3, 3, 3)))
    }

    class MaxNumber {

        fun maxNumber(listOf: List<Int>): Int? {
            return listOf.minOrNull()
        }

    }
}
