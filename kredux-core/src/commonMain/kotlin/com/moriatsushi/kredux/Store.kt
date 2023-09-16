package com.moriatsushi.kredux

import com.moriatsushi.kredux.internal.CombinedMiddleware
import com.moriatsushi.kredux.internal.StoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

/**
 * A store that holds a [state][State]. The only way to change the state is to [dispatch] an
 * [action][Action].
 *
 * **Do not implement this interface directly.** New methods or properties might be added to this
 * interface in the future. Use the `Store()` function to create a [Store].
 */
interface Store<out State, in Action : Any> {
    /**
     * A [StateFlow] of the current state.
     */
    val state: StateFlow<State>

    /**
     * Dispatches an [action][Action].
     */
    fun dispatch(action: Action)
}

/**
 * Creates a [Store] that holds the state.
 *
 * @param reducer A [Reducer] that returns the next state, given the current state and an action to
 * handle.
 * @param initialState An initial state. If not specified, the [Reducer.initialState] is used.
 * @param middlewares A list of [Middleware]s that can wrap the [Store.dispatch] method.
 * @param coroutineScope A [CoroutineScope] of the [Store]. If not specified, a global [CoroutineScope]
 * is created.
 * @return A [Store] that holds the state.
 */
fun <State, Action : Any> Store(
    reducer: Reducer<State, Action>,
    initialState: State = reducer.initialState,
    middlewares: List<Middleware<State, Action>> = emptyList(),
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
): Store<State, Action> = StoreImpl(
    reducer = reducer,
    initialState = initialState,
    middleware = CombinedMiddleware(middlewares),
    coroutineScope = coroutineScope,
)
