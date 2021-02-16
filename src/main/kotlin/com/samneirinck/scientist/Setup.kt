@file:JvmName("Setup")
package com.samneirinck.scientist

import kotlin.properties.Delegates

class ExperimentSetup<T, C> {

    private var name: String = "default-experiment"
    private var control: Trial<T> by Delegates.notNull()
    private var candidates: List<Trial<T>> = mutableListOf()
    private var conductible: (com.samneirinck.scientist.ContextProvider<C>) -> Boolean = { true }
    private var catches: (Throwable) -> Boolean = { true }

    fun name(name: () -> String) = apply { this.name = name() }
    fun experiment(name: () -> String) = name(name)

    fun control(name: String = "control", behaviour: Behaviour<T>) = apply {
        control = Trial(name = name, behaviour = behaviour)
    }

    fun candidate(name: String = "candidate", behaviour: Behaviour<T>) = apply {
        candidates += Trial(name = name, behaviour = behaviour)
    }

    fun conductibleIf(predicate: (com.samneirinck.scientist.ContextProvider<C>) -> Boolean) = apply {
        conductible = predicate
    }

    fun catches(catcher: (Throwable) -> Boolean) = apply {
        catches = catcher
    }

    internal fun complete() = com.samneirinck.scientist.DefaultExperiment(
        name = name,
        control = control.copy(catches = catches),
        candidates = candidates.map { it.copy(catches = catches) },
        conductible = conductible
    )

}

class ScientistSetup<T, C> {

    private var contextProvider: com.samneirinck.scientist.ContextProvider<C> =
        com.samneirinck.scientist.NotImplementedContextProvider()
    private var publish: Publisher<T, C> = NoPublisher()
    private var ignores: List<com.samneirinck.scientist.Matcher<T>> = mutableListOf()
    private var matcher: com.samneirinck.scientist.Matcher<T> = com.samneirinck.scientist.DefaultMatcher()
    private var throwOnMismatches: Boolean = false

    fun publisher(publisher: Publisher<T, C>) = apply {
        publish = publisher
    }

    fun ignore(ignore: com.samneirinck.scientist.Matcher<T>) = apply {
        this.ignores += ignore
    }

    fun match(matcher: com.samneirinck.scientist.Matcher<T>) = apply {
        this.matcher = matcher
    }

    fun context(contextProvider: com.samneirinck.scientist.ContextProvider<C>) = apply {
        this.contextProvider = contextProvider
    }

    fun throwOnMismatches(throwOnMismatches: () -> Boolean) = apply {
        this.throwOnMismatches = throwOnMismatches()
    }

    internal fun complete() = Scientist(
            contextProvider = contextProvider,
            publish = publish,
            ignores = ignores,
            matcher = matcher,
            throwOnMismatches = throwOnMismatches
    )

}

suspend infix fun <T, C> Scientist<T, C>.conduct(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): T =
        this.evaluate(experiment(setup))

suspend infix fun <T, C> Scientist<T, C>.conduct(experiment: com.samneirinck.scientist.Experiment<T, C>): T = this.evaluate(experiment)

fun <T, C> experiment(setup: ExperimentSetup<T, C>.() -> ExperimentSetup<T, C>): com.samneirinck.scientist.Experiment<T, C>
        = setup(ExperimentSetup()).complete()

fun <T, C> scientist(setup: ScientistSetup<T, C>.() -> ScientistSetup<T, C>): Scientist<T, C>
        = setup(ScientistSetup()).complete()

fun <T, C> scientist(): Scientist<T, C>
        = ScientistSetup<T, C>().complete()


fun <T> scientist(setup: ExperimentSetup<T, Unit>.() -> ExperimentSetup<T, Unit>): com.samneirinck.scientist.Experiment<T, Unit>
    = setup(ExperimentSetup()).complete()
