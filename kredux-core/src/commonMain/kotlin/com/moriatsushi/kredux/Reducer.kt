package com.moriatsushi.kredux

import com.moriatsushi.kredux.internal.ReducerImpl

/**
 * A reducer that returns the next state, given the current state and an action to handle.
 *
 * It is not recommended to implement this interface directly. Use the `Reducer()` function to
 * create a [Reducer].
 */
interface Reducer<State, in Action : Any> {
    /**
     * An initial state.
     */
    val initialState: State

    /**
     * Returns the next state, given the [current state][acc] and an [action] to handle.
     */
    fun reduce(acc: State, action: Action): State
}

/**
 * Creates a [Reducer] that returns the next state, given the current state and an action to handle.
 *
 * An example of a reducer that represents a counter:
 * ```
 * val reducer = Reducer(0) { acc, action: Action ->
 *    when (action) {
 *        is Action.Increment -> acc + 1
 *        is Action.Decrement -> acc - 1
 *    }
 * }
 * ```
 */
fun <State, Action : Any> Reducer(
    initialState: State,
    reducer: (acc: State, action: Action) -> State,
): Reducer<State, Action> {
    return ReducerImpl(
        initialState = initialState,
        reducer = reducer,
    )
}
