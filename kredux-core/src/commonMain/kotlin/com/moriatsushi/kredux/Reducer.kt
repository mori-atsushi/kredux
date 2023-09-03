package com.moriatsushi.kredux

import com.moriatsushi.kredux.internal.ReducerImpl

interface Reducer<State, in Action : Any> {
    val initialState: State

    fun reduce(acc: State, action: Action): State
}

fun <State, Action : Any> Reducer(
    initialState: State,
    reducer: (acc: State, action: Action) -> State,
): Reducer<State, Action> {
    return ReducerImpl(
        initialState = initialState,
        reducer = reducer,
    )
}
