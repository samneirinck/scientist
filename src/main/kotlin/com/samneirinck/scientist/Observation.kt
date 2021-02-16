package com.samneirinck.scientist

import java.time.Duration
import java.time.Instant

data class Observation<T>(
    val id: String,
    val name: String,
    val outcome: com.samneirinck.scientist.Outcome<T>,
    val startedAt: Instant,
    val stoppedAt: Instant
) {

    val duration: Duration by lazy {
        Duration.between(startedAt, stoppedAt)
    }

    val result: T by lazy {
        when(outcome) {
            is com.samneirinck.scientist.Success<T> -> outcome.value
            is com.samneirinck.scientist.Failure<T> -> throw outcome.throwable
        }
    }

    fun matches(other: com.samneirinck.scientist.Observation<T>, match: com.samneirinck.scientist.Matcher<T>) = match(outcome, other.outcome)

    fun isIgnored(other: com.samneirinck.scientist.Observation<T>, ignores: List<com.samneirinck.scientist.Matcher<T>>) = ignores.any { it(outcome, other.outcome) }

}
