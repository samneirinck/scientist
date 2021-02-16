package com.samneirinck.scientist


class MismatchException(experimentName: String) : RuntimeException("Experiment $experimentName observations mismatched")
