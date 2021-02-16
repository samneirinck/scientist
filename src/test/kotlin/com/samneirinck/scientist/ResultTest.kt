package com.samneirinck.scientist

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test
import java.time.Instant


class ResultTest {

    private val contextProvider = object : com.samneirinck.scientist.ContextProvider<Unit> {
        override fun invoke() = Unit
    }

    private val baseObservation = com.samneirinck.scientist.Observation(
        id = "base-id",
        name = "base-test",
        outcome = Success(true),
        startedAt = Instant.now(),
        stoppedAt = Instant.now()
    )

    private val baseResult = Result(
            experimentName = "test",
            observations = emptyList(),
            controlObservation = baseObservation.copy(id = "control-id", name = "control-test"),
            candidateObservations = listOf(baseObservation.copy(id = "candidate-id", name = "candidate-test")),
            mismatches = emptyList(),
            ignoredMismatches = emptyList(),
            contextProvider = contextProvider
    )

    @Test
    fun `test matched`() {
        val result = baseResult.copy()

        result.matched.shouldBeTrue()
        result.ignored.shouldBeFalse()
        result.mismatched.shouldBeFalse()
    }

    @Test
    fun `test ignored`()  {
        val result = baseResult.copy(ignoredMismatches = listOf(baseObservation))

        result.matched.shouldBeFalse()
        result.ignored.shouldBeTrue()
        result.mismatched.shouldBeFalse()
    }

    @Test
    fun `test mismatched`()  {
        val result = baseResult.copy(mismatches = listOf(baseObservation))

        result.matched.shouldBeFalse()
        result.ignored.shouldBeFalse()
        result.mismatched.shouldBeTrue()
    }

}
