package com.moriatsushi.kredux

interface Reducer<State, in Action : Any> {
    val initialState: State

    fun reduce(acc: State, action: Action): State
}

fun <State, Action : Any> createReducer(
    initial: State,
    reducer: (acc: State, action: Action) -> State,
): Reducer<State, Action> {
    return object : Reducer<State, Action> {
        override val initialState: State = initial

        override fun reduce(acc: State, action: Action): State {
            return reducer(acc, action)
        }
    }
}
