package com.github.spoptchev.scientist

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ExperimentTest {

    private val controlTrial = Trial(name = "control-trial") { true }
    private val candidateTrial = Trial(name = "candidate-trial") { false }
    private val contextProvider = object : ContextProvider<Unit> {
        override fun invoke() = Unit
    }

    private val baseExperiment = DefaultExperiment<Boolean, Unit>(
            name = "test",
            control = controlTrial,
            candidates = listOf(candidateTrial)
    )

    @Test
    fun `should return a skipped experiment state when not conductible`() {
        val experiment = baseExperiment.copy(conductible = { false })
        val state = runBlocking { experiment.conduct(contextProvider) }

        (state is Skipped).shouldBeTrue()
    }

    @Test
    fun `should return a conducted experiment when its conductible`() {
        val experiment = baseExperiment.copy(conductible = { true })
        val state = runBlocking { experiment.conduct(contextProvider) }

        (state is Conducted).shouldBeTrue()
    }

    @Test
    fun `refresh should update the ids of the control and candidate trials`() {
        val controlId = baseExperiment.control.id
        val candidateIds = baseExperiment.candidates.map { it.id }

        val refreshedExperiment = baseExperiment.refresh() as DefaultExperiment

        val newControlId = refreshedExperiment.control.id
        val newCandidateIds = refreshedExperiment.candidates.map { it.id }

        newControlId.shouldNotBe(controlId)
        newCandidateIds.shouldNotBe(candidateIds)
    }

}
