package com.samneirinck.scientist

sealed class ExperimentState<T>
data class Skipped<T>(val observation: com.samneirinck.scientist.Observation<T>) : com.samneirinck.scientist.ExperimentState<T>()
data class Conducted<T>(
    val name: String,
    val observations: List<com.samneirinck.scientist.Observation<T>>,
    val controlObservation: com.samneirinck.scientist.Observation<T>,
    val candidateObservations: List<com.samneirinck.scientist.Observation<T>>
) : com.samneirinck.scientist.ExperimentState<T>()
