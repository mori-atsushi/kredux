package com.moriatsushi.kredux

fun <State, Action : Any, S1, S2> combineReducers(
    reducer1: ChildReducer<State, Action, S1>,
    reducer2: ChildReducer<State, Action, S2>,
    transform: (S1, S2) -> State,
): Reducer<State, Action> {
    return combineReducersUnsafe(reducer1, reducer2) {
        @Suppress("UNCHECKED_CAST")
        transform(it[0] as S1, it[1] as S2)
    }
}

fun <State, Action : Any, S1, S2, S3> combineReducers(
    reducer1: ChildReducer<State, Action, S1>,
    reducer2: ChildReducer<State, Action, S2>,
    reducer3: ChildReducer<State, Action, S3>,
    transform: (S1, S2, S3) -> State,
): Reducer<State, Action> {
    return combineReducersUnsafe(reducer1, reducer2, reducer3) {
        @Suppress("UNCHECKED_CAST")
        transform(it[0] as S1, it[1] as S2, it[2] as S3)
    }
}

fun <State, Action : Any, S1, S2, S3, S4> combineReducers(
    reducer1: ChildReducer<State, Action, S1>,
    reducer2: ChildReducer<State, Action, S2>,
    reducer3: ChildReducer<State, Action, S3>,
    reducer4: ChildReducer<State, Action, S4>,
    transform: (S1, S2, S3, S4) -> State,
): Reducer<State, Action> {
    return combineReducersUnsafe(reducer1, reducer2, reducer3, reducer4) {
        @Suppress("UNCHECKED_CAST")
        transform(
            it[0] as S1,
            it[1] as S2,
            it[2] as S3,
            it[3] as S4,
        )
    }
}

fun <State, Action : Any, S1, S2, S3, S4, S5> combineReducers(
    reducer1: ChildReducer<State, Action, S1>,
    reducer2: ChildReducer<State, Action, S2>,
    reducer3: ChildReducer<State, Action, S3>,
    reducer4: ChildReducer<State, Action, S4>,
    reducer5: ChildReducer<State, Action, S5>,
    transform: (S1, S2, S3, S4, S5) -> State,
): Reducer<State, Action> {
    return combineReducersUnsafe(reducer1, reducer2, reducer3, reducer4, reducer5) {
        @Suppress("UNCHECKED_CAST")
        transform(
            it[0] as S1,
            it[1] as S2,
            it[2] as S3,
            it[3] as S4,
            it[4] as S5,
        )
    }
}

fun <State, Action : Any, S> combineReducers(
    vararg reducers: ChildReducer<State, Action, S>,
    transform: (List<S>) -> State,
): Reducer<State, Action> {
    return CombinedReducer(reducers, transform)
}

private fun <State, Action : Any> combineReducersUnsafe(
    vararg reducers: ChildReducer<State, Action, *>,
    transform: (List<*>) -> State,
): Reducer<State, Action> {
    return CombinedReducer(reducers, transform)
}

interface ChildReducer<in ParentState, in Action : Any, out ChildState> {
    val initialState: ChildState

    fun reduce(parent: ParentState, action: Action): ChildState
}

fun <ParentState, ParentAction : Any, ChildState> child(
    reducer: Reducer<ChildState, ParentAction>,
    mapToChildState: (parent: ParentState) -> ChildState,
): ChildReducer<ParentState, ParentAction, ChildState> {
    return object : ChildReducer<ParentState, ParentAction, ChildState> {
        override val initialState: ChildState = reducer.initialState

        override fun reduce(parent: ParentState, action: ParentAction): ChildState {
            val childState = mapToChildState(parent)
            return reducer.reduce(childState, action)
        }
    }
}

fun <ParentState, ParentAction : Any, ChildState, ChildAction : Any> child(
    reducer: Reducer<ChildState, ChildAction>,
    mapToChildAction: (parent: ParentAction) -> ChildAction?,
    mapToChildState: (parent: ParentState) -> ChildState,
): ChildReducer<ParentState, ParentAction, ChildState> {
    return object : ChildReducer<ParentState, ParentAction, ChildState> {
        override val initialState: ChildState = reducer.initialState

        override fun reduce(parent: ParentState, action: ParentAction): ChildState {
            val childState = mapToChildState(parent)
            val childAction = mapToChildAction(action) ?: return childState
            return reducer.reduce(childState, childAction)
        }
    }
}

private class CombinedReducer<State, in Action : Any, out S>(
    private val items: Array<out ChildReducer<State, Action, S>>,
    private val transform: (List<S>) -> State,
) : Reducer<State, Action> {
    override val initialState: State =
        transform(items.map { it.initialState })

    override fun reduce(acc: State, action: Action): State {
        return transform(items.map { it.reduce(acc, action) })
    }
}
