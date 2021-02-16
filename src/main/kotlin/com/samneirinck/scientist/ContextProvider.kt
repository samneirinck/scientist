package com.samneirinck.scientist

typealias ContextProvider<C> = () -> C

object NoContextProvider : com.samneirinck.scientist.ContextProvider<Unit> {
    override fun invoke() {}
}

class NotImplementedContextProvider<out C> : com.samneirinck.scientist.ContextProvider<C> {
    override fun invoke(): C = TODO("not implemented")
}
