package com.samneirinck.scientist

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.Test


class ScientistSetupTest {

    private val setup = ScientistSetup<Int, Unit>()

    @Test
    fun `test change publisher`() {
        val publisher = { _ : Result<Int, Unit> -> }
        val scientist = setup
                .publisher(publisher)
                .complete()

        scientist.publish.shouldBe(publisher)
    }

    @Test
    fun `test add ignore`() {
        val ignore = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val scientist = setup
                .ignore(ignore)
                .ignore({ _: Outcome<Int>, _: Outcome<Int> -> true })
                .complete()

        scientist.ignores.first().shouldBe(ignore)
        scientist.ignores.size.shouldBe(2)
    }

    @Test
    fun `test change matcher`() {
        val matcher = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val scientist = setup
                .match(matcher)
                .complete()

        scientist.matcher.shouldBe(matcher)
    }

    @Test
    fun `test context provider`() {
        val scientist = setup
                .context(com.samneirinck.scientist.NoContextProvider)
                .complete()

        scientist.contextProvider.shouldBe(com.samneirinck.scientist.NoContextProvider)
    }

    @Test
    fun `test throw on mismatches not set`() {
        val scientist = setup.complete()

        scientist.throwOnMismatches.shouldBeFalse()
    }

    @Test
    fun `test throw on mismatches`() {
        val scientist = setup
                .throwOnMismatches { true }
                .complete()

        scientist.throwOnMismatches.shouldBeTrue()
    }

}
