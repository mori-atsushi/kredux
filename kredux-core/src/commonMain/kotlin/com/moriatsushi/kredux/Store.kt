package com.moriatsushi.kredux

import com.moriatsushi.kredux.internal.CombinedMiddleware
import com.moriatsushi.kredux.internal.StoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

interface Store<out State, in Action : Any> {
    val state: StateFlow<State>
    fun dispatch(action: Action)
}

fun <State, Action : Any> createStore(
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
