package com.github.spoptchev.scientist

import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals


class TrialTest {

    private val trial = Trial("test", "test") {
        Thread.sleep(20)
        true
    }

    @Test
    fun `test observe`() {
        val observation = trial.run()

        assert(observation.duration >= Duration.ofMillis(20))
        assertEquals(observation.name, "test")
        assert(observation.startedAt < observation.stoppedAt)
    }

}
