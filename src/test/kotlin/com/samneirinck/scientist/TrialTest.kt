package com.samneirinck.scientist

import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Duration


class TrialTest {

    private val trial = Trial("test", "test") {
        Thread.sleep(20)
        true
    }

    private val exceptionTrial = Trial("test", "test") {
        throw NumberFormatException("e")
    }

    @Test
    fun `test observe`() {
        val observation = runBlocking { trial.run() }

        assert(observation.duration >= Duration.ofMillis(20))
        observation.name.shouldBe("test")
        assert(observation.startedAt < observation.stoppedAt)
    }

    @Test
    fun `test successful outcome`() {
        val observation = runBlocking { trial.run() }

        observation.outcome.shouldBe(Success(true))
    }

    @Test
    fun `test failure outcome`() {
        val observation = runBlocking { exceptionTrial.run() }

        observation.outcome.isFailure().shouldBeTrue()
    }

    @Test(expected = NumberFormatException::class)
    fun `test throw exception when not caught`() {
        val trial = exceptionTrial.copy(catches = { e -> e is NullPointerException })

        runBlocking { trial.run() }

        fail("expected to throw NumberFormatException")
    }

    @Test
    fun `test refresh to generate a new id`() {
        val currentId = trial.id
        val newId = trial.refresh().id

        newId.shouldNotBe(currentId)
    }

    @Test
    fun `test compareTo`() {
        val trial1 = Trial(id = "Z", name = "test") { }
        val trial2 = Trial(id = "A", name = "test") { }
        val trial3 = Trial(id = "A", name = "test") { }

        trial1.shouldBeGreaterThan(trial2)
        trial2.shouldBeGreaterThanOrEqualTo(trial3)
    }

}
