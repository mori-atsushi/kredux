package com.moriatsushi.kredux.internal

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
    coroutineScope: CoroutineScope,
) : Store<State, Action> {
    private val actionChannel = Channel<Action>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(reducer.initialState)
    override val state: StateFlow<State> = _state.asStateFlow()

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
        _state.value = reducer.reduce(_state.value, action)
    }
}