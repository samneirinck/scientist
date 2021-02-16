package com.samneirinck.scientist

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test


class OutcomeTest {

    private val success = Success(true)
    private val failure = Failure<Boolean>(RuntimeException("test"))

    @Test
    fun `test success`() {
        success.isSuccess().shouldBeTrue()
        success.isFailure().shouldBeFalse()
    }

    @Test
    fun `test failure`() {
        failure.isSuccess().shouldBeFalse()
        failure.isFailure().shouldBeTrue()
    }
}
