package com.moriatsushi.kredux.internal

import com.moriatsushi.kredux.Middleware
import com.moriatsushi.kredux.MiddlewareScope
import com.moriatsushi.kredux.Reducer
import com.moriatsushi.kredux.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class StoreImpl<State, Action : Any>(
    private val reducer: Reducer<State, Action>,
    initialState: State,
    private val middleware: Middleware<State, Action>,
    coroutineScope: CoroutineScope,
) : Store<State, Action> {
    private val actionChannel = Channel<Action>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state.asStateFlow()

    private val middlewareScope = object : MiddlewareScope<State, Action> {
        override val coroutineScope: CoroutineScope = coroutineScope
        override val state: State get() = _state.value

        override fun next(action: Action): State {
            return reducer.reduce(state, action)
        }

        override fun dispatch(action: Action) {
            this@StoreImpl.dispatch(action)
        }
    }

    init {
        coroutineScope.launch {
            for (action in actionChannel) {
                reduce(action)
            }
        }
    }

    override fun dispatch(action: Action) {
        actionChannel.trySend(action)
    }

    private fun reduce(action: Action) {
        with(middleware) {
            _state.value = middlewareScope.process(action)
        }
    }
}
