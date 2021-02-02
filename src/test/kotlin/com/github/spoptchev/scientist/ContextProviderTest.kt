package com.github.spoptchev.scientist

import io.kotest.matchers.shouldBe
import org.junit.Test


class ContextProviderTest {

    @Test
    fun `test no context provider`() {
        val contextProvider = NoContextProvider
        val result = contextProvider()

        result.shouldBe(Unit)
    }

    @Test(expected = NotImplementedError::class)
    fun `test not implemented context provider`() {
        val contextProvider = NotImplementedContextProvider<Boolean>()

        contextProvider()
    }

}
