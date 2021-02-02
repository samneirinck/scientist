package com.github.spoptchev.scientist

import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test


class ScientistTest {

    class CatchingPublisher(var caughtResult: Result<Boolean, Unit>?) : Publisher<Boolean, Unit> {
        override fun invoke(result: Result<Boolean, Unit>) {
            caughtResult = result
        }
    }

    private val contextProvider = NoContextProvider
    private val exception = RuntimeException("Test")
    private val controlTrial = Trial(name = "control-trial") { true }
    private val candidateTrial = Trial(name = "candidate-trial") { false }
    private val exceptionTrial = Trial<Boolean>(name = "candidate-exception") { throw exception }

    private val baseExperiment = DefaultExperiment<Boolean, Unit>(
        name = "test",
        control = controlTrial,
        candidates = listOf(candidateTrial, exceptionTrial)
    )

    private val ignore = { candidate: Outcome<Boolean>, _: Outcome<Boolean> -> candidate.isFailure() }

    private val scientist = Scientist(
        contextProvider = contextProvider,
        ignores = listOf(ignore)
    )

    @Test
    fun `test evaluate experiment`() {
        val publisher = CatchingPublisher(null)

        val result = runBlocking {
            scientist
                .copy(publish = publisher)
                .evaluate(baseExperiment)
        }

        val publishedResult = publisher.caughtResult!!

        result.shouldBeTrue()
        publishedResult.observations.size.shouldBe(3)
        publishedResult.candidateObservations.size.shouldBe(2)
        publishedResult.mismatches.size.shouldBe(1)
        publishedResult.ignoredMismatches.size.shouldBe(1)
        publishedResult.controlObservation.outcome.shouldBe(Success(true))
        publishedResult.candidateObservations.map { it.outcome }.contains(Success(false)).shouldBeTrue()
        publishedResult.ignoredMismatches.first().outcome.shouldBe(Failure(exception))
        publishedResult.contextProvider.shouldBe(contextProvider)
        publishedResult.mismatched.shouldBeTrue()
        publishedResult.ignored.shouldBeTrue()
        publishedResult.matched.shouldBeFalse()
    }

    @Test(expected = MismatchException::class)
    fun `test throw on mismatches`() {
        runBlocking {
            scientist
                .copy(throwOnMismatches = true)
                .evaluate(baseExperiment)
        }
        fail("Expected to fail with MismatchException")
    }

    @Test
    fun `test parallel execution`() = runBlocking<Unit> {
        suspend fun control(): Boolean {
            delay(100)
            return true
        }

        suspend fun slowCandidate(): Boolean {
            delay(2_000)
            return false
        }

        fun faultyCandidate(): Boolean {
            throw Exception("Failure")
        }

        val publisher = CatchingPublisher(null)
        val result = scientist
            .copy(publish = publisher)
            .conduct {
                control { control() }
                candidate(name = "slow") { slowCandidate() }
                candidate(name = "faulty" ) { faultyCandidate() }
            }

        result.shouldBe(true)
        val publishedResult = publisher.caughtResult
        publishedResult.shouldNotBeNull()

        publishedResult.mismatched.shouldBeTrue()
        publishedResult.mismatches.size.shouldBe(1)
        publishedResult.ignoredMismatches.size.shouldBe(1)

        val slowCandidate = publishedResult.candidateObservations.single { it.name == "slow" }
        val faultyCandidate = publishedResult.candidateObservations.single { it.name == "faulty" }

        slowCandidate.result.shouldBeFalse()
        faultyCandidate.outcome.shouldBeInstanceOf<Failure<*>>()
    }

}
