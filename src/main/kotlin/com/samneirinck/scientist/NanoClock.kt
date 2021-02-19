package com.samneirinck.scientist

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class NanoClock(private val clock: Clock = systemUTC()) : Clock() {

    private val initialInstant = Instant.now(clock)
    private val initialNanos = System.nanoTime()

    override fun withZone(zone: ZoneId): Clock = NanoClock(clock.withZone(zone))

    override fun getZone(): ZoneId = clock.zone

    override fun instant(): Instant = initialInstant.plusNanos(System.nanoTime() - initialNanos)

}
