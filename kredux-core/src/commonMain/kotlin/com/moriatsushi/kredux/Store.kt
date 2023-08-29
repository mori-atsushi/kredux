package com.moriatsushi.kredux

import com.moriatsushi.kredux.internal.StoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface Store<out State, in Action : Any> {
    val state: StateFlow<State>
    fun dispatch(action: Action)
}

fun <State, Action : Any> createStore(
    reducer: Reducer<State, Action>,
    coroutineScope: CoroutineScope,
): Store<State, Action> = StoreImpl(
    reducer = reducer,
    coroutineScope = coroutineScope,
)
