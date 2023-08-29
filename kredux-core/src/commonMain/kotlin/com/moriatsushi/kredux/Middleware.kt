package com.moriatsushi.kredux

import kotlinx.coroutines.CoroutineScope

interface Middleware<State, Action : Any> {
    fun MiddlewareScope<State, Action>.process(action: Action): State
}

interface MiddlewareScope<State, Action : Any> {
    val state: State
    val coroutineScope: CoroutineScope

    fun next(action: Action): State
    fun dispatch(action: Action)
}
