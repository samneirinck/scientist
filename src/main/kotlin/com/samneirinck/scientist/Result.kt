package com.samneirinck.scientist

data class Result<T, out C>(
    val experimentName: String,
    val observations: List<com.samneirinck.scientist.Observation<T>>,
    val controlObservation: com.samneirinck.scientist.Observation<T>,
    val candidateObservations: List<com.samneirinck.scientist.Observation<T>>,
    val mismatches: List<com.samneirinck.scientist.Observation<T>>,
    val ignoredMismatches: List<com.samneirinck.scientist.Observation<T>>,
    val contextProvider: com.samneirinck.scientist.ContextProvider<C>
) {

    val matched: Boolean by lazy { !mismatched && !ignored }
    val mismatched: Boolean by lazy { mismatches.isNotEmpty() }
    val ignored: Boolean by lazy { ignoredMismatches.isNotEmpty() }

}
