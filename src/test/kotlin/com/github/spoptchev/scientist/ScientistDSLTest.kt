package com.github.spoptchev.scientist

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ScientistDSLTest {

    @Test
    fun `test scientist dsl with scientist and experiment setup`() = runBlocking {
        val result = scientist<Int, Unit> {
            context {}
        } conduct {
            experiment { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        result.shouldBe(1)
    }

    @Test
    fun `test default scientist`() = runBlocking {
        val result = scientist<Int, Unit>() conduct {
            experiment { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        result.shouldBe(1)

    }

    @Test
    fun `test with experiment`() = runBlocking {
        val result = scientist<Int, Unit>() conduct experiment {
            name { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        result.shouldBe(1)
    }

}
