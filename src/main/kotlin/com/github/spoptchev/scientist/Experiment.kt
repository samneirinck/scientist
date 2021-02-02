package com.github.spoptchev.scientist

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface Experiment<T, in C> {
    suspend fun conduct(contextProvider: ContextProvider<C>): ExperimentState<T>
    fun refresh(): Experiment<T, C>
}

data class DefaultExperiment<T, in C>(
        val name: String,
        val control: Trial<T>,
        val candidates: List<Trial<T>>,
        val conductible: (ContextProvider<C>) -> Boolean = { true }
) : Experiment<T, C> {

    private val shuffledTrials: List<Trial<T>> by lazy {
        (candidates + control).sorted()
    }

    override suspend fun conduct(contextProvider: ContextProvider<C>): ExperimentState<T> {
        return if (conductible(contextProvider)) {
            coroutineScope {
                val observations = shuffledTrials
                    .map { async { it.run() } }
                    .map { it.await() }

                val controlObservation = observations.first { it.id == control.id }
                val candidateObservations = observations - controlObservation


            Conducted(name, observations, controlObservation, candidateObservations)
            }

        } else {
            Skipped(control.run())
        }
    }

    override fun refresh(): Experiment<T, C> = copy(
            control = control.refresh(),
            candidates = candidates.map { it.refresh() }
    )

}
