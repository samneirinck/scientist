package com.samneirinck.scientist

import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId


class ObservationTest {

    private val fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    private val value = 1
    private val outcome = Success(value)
    private val startedAt = Instant.now(fixedClock)

    private val baseObservation = Observation(
        id = "test",
        name = "test",
        outcome = outcome,
        startedAt = startedAt,
        stoppedAt = startedAt
    )

    class IgnoreCandidateFailures : Matcher<Int> {
        override fun invoke(candidate: Outcome<Int>, control: Outcome<Int>) = candidate.isFailure()
    }

    @Test
    fun `test duration`() {
        val observation = baseObservation.copy(stoppedAt = startedAt.plusNanos(1))
        val expectedDuration = Duration.ofNanos(1)

        observation.duration.shouldBe(expectedDuration)
    }

    @Test
    fun `test successful result`() {
        val observation = baseObservation.copy(outcome = Success(2))

        observation.result.shouldBe(2)
    }

    @Test(expected = RuntimeException::class)
    fun `test result failure`() {
        val observation = baseObservation.copy(outcome = Failure(RuntimeException()))

        observation.result

        fail("expected RuntimeException")
    }

    @Test
    fun `test match`() {
        val observation1 = baseObservation.copy(outcome = Success(1))
        val observation2 = baseObservation.copy(outcome = Success(1))

        observation1.matches(observation2, DefaultMatcher()).shouldBeTrue()
    }

    @Test
    fun `test isIgnored`() {
        val matcher = IgnoreCandidateFailures()

        val observation1 = baseObservation.copy(outcome = Success(1))
        val observation2 = baseObservation.copy(outcome = Failure(RuntimeException("test")))

        observation2.isIgnored(observation1, listOf(matcher)).shouldBeTrue()
    }

}
