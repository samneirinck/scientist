package com.samneirinck.scientist

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


class NanoClockTest {

    private val fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    @Test
    fun `test withZone`() {
        val nanoClock = NanoClock(fixedClock)
        val zone = ZoneId.of("Europe/Berlin")
        val result = nanoClock.withZone(zone)

        result.zone.shouldBe(zone)
    }

}
