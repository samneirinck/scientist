package com.github.spoptchev.scientist

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.Test

class ExperimentSetupTest {

    private val setup = ExperimentSetup<Int, Unit>()
            .control("default-control") { 2 }

    @Test
    fun `test change control`() {
        val experiment = setup
                .control("changed") { 1 }
                .complete()

        experiment.control.name.shouldBe("changed")
    }

    @Test
    fun `test add candidate`() {
        val experiment = setup
                .candidate { 2 }
                .candidate { 3 }
                .complete()

        experiment.candidates.size.shouldBe(2)
    }

    @Test
    fun `test change conductible`() {
        val context = NoContextProvider
        val experiment = setup
                .conductibleIf { false }
                .complete()

        experiment.conductible(context).shouldBeFalse()
    }

    @Test
    fun `test experiment name`() {
        val experiment = setup
                .experiment { "some-experiment" }
                .complete()

        experiment.name.shouldBe("some-experiment")
    }

    @Test
    fun `test catches`() {
        val experiment = setup
                .control { 1 }
                .candidate { 1 }
                .catches { e: Throwable -> e is NumberFormatException }
                .complete()

        val e1 = NumberFormatException("")
        val e2 = NullPointerException("")

        experiment.control.catches(e1).shouldBeTrue()
        experiment.candidates.all { it.catches(e1) }.shouldBeTrue()
        experiment.control.catches(e2).shouldBeFalse()
        experiment.candidates.all { it.catches(e2) }.shouldBeFalse()
    }

}
