package com.samneirinck.scientist

data class Scientist<T, C>(
    val contextProvider: com.samneirinck.scientist.ContextProvider<C>,
    val publish: Publisher<T, C> = NoPublisher(),
    val ignores: List<com.samneirinck.scientist.Matcher<T>> = emptyList(),
    val matcher: com.samneirinck.scientist.Matcher<T> = com.samneirinck.scientist.DefaultMatcher(),
    val throwOnMismatches: Boolean = false
) {

    suspend fun evaluate(experiment: com.samneirinck.scientist.Experiment<T, C>): T {
        val experimentState = experiment
                .refresh()
                .conduct(contextProvider)

        return when(experimentState) {
            is com.samneirinck.scientist.Skipped -> experimentState.observation.result
            is com.samneirinck.scientist.Conducted -> {
                val (experimentName, observations, controlObservation, candidateObservations) = experimentState

                val allMismatches = candidateObservations.filterNot { it.matches(controlObservation, matcher) }
                val ignoredMismatches = allMismatches.filter { it.isIgnored(controlObservation, ignores) }
                val mismatches = allMismatches - ignoredMismatches

                val result = Result(
                        experimentName = experimentName,
                        observations = observations,
                        controlObservation = controlObservation,
                        candidateObservations = candidateObservations,
                        mismatches = mismatches,
                        ignoredMismatches = ignoredMismatches,
                        contextProvider = contextProvider
                )

                publish(result)

                if (throwOnMismatches && result.mismatched) {
                    throw com.samneirinck.scientist.MismatchException(experimentName)
                }

                controlObservation.result
            }
        }
    }

}
