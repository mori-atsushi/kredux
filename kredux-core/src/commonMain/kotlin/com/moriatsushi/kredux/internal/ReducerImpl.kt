package com.moriatsushi.kredux.internal

import com.moriatsushi.kredux.Reducer

internal class ReducerImpl<State, in Action : Any>(
    override val initialState: State,
    private val reducer: (acc: State, action: Action) -> State,
) : Reducer<State, Action> {
    override fun reduce(acc: State, action: Action): State {
        return reducer(acc, action)
    }
}
