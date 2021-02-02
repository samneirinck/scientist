package com.github.spoptchev.scientist

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test


class MatcherTest {

    private val success1 = Success(true)
    private val success2 = Success(false)
    private val failure1 = Failure<Boolean>(RuntimeException("failure1"))
    private val failure2 = Failure<Boolean>(RuntimeException("failure2"))
    private val match = DefaultMatcher<Boolean>()

    @Test
    fun `test when both success and same value`() {
        val result = match(success1, Success(true))

        result.shouldBeTrue()
    }

    @Test
    fun `test when both success but different value`() {
        val result = match(success1, success2)

        result.shouldBeFalse()
    }

    @Test
    fun `test when one success and other failure`() {
        val result = match(success1, failure1)

        result.shouldBeFalse()
    }

    @Test
    fun `test when both failure with same error message`() {
        val result = match(failure1, Failure(RuntimeException("failure1")))

        result.shouldBeTrue()
    }

    @Test
    fun `test when both failure with different error message`() {
        val result = match(failure1, failure2)

        result.shouldBeFalse()
    }

}
