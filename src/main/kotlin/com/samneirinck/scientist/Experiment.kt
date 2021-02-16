package com.samneirinck.scientist

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface Experiment<T, in C> {
    suspend fun conduct(contextProvider: com.samneirinck.scientist.ContextProvider<C>): com.samneirinck.scientist.ExperimentState<T>
    fun refresh(): com.samneirinck.scientist.Experiment<T, C>
}

data class DefaultExperiment<T, in C>(
    val name: String,
    val control: com.samneirinck.scientist.Trial<T>,
    val candidates: List<com.samneirinck.scientist.Trial<T>>,
    val conductible: (com.samneirinck.scientist.ContextProvider<C>) -> Boolean = { true }
) : com.samneirinck.scientist.Experiment<T, C> {

    private val shuffledTrials: List<com.samneirinck.scientist.Trial<T>> by lazy {
        (candidates + control).sorted()
    }

    override suspend fun conduct(contextProvider: com.samneirinck.scientist.ContextProvider<C>): com.samneirinck.scientist.ExperimentState<T> {
        return if (conductible(contextProvider)) {
            coroutineScope {
                val observations = shuffledTrials
                    .map { async { it.run() } }
                    .map { it.await() }

                val controlObservation = observations.first { it.id == control.id }
                val candidateObservations = observations - controlObservation


                com.samneirinck.scientist.Conducted(name, observations, controlObservation, candidateObservations)
            }

        } else {
            com.samneirinck.scientist.Skipped(control.run())
        }
    }

    override fun refresh(): com.samneirinck.scientist.Experiment<T, C> = copy(
            control = control.refresh(),
            candidates = candidates.map { it.refresh() }
    )

}
