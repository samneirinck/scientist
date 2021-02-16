package com.samneirinck.scientist


typealias Matcher<T> = (com.samneirinck.scientist.Outcome<T>, com.samneirinck.scientist.Outcome<T>) -> Boolean

class DefaultMatcher<in T> : com.samneirinck.scientist.Matcher<T> {
    override fun invoke(candidate: com.samneirinck.scientist.Outcome<T>, control: com.samneirinck.scientist.Outcome<T>): Boolean = when(candidate) {
        is com.samneirinck.scientist.Success -> when(control) {
            is com.samneirinck.scientist.Success -> candidate.value == control.value
            is com.samneirinck.scientist.Failure -> false
        }
        is com.samneirinck.scientist.Failure -> when(control) {
            is com.samneirinck.scientist.Success -> false
            is com.samneirinck.scientist.Failure -> candidate.errorMessage == control.errorMessage
        }
    }
}
