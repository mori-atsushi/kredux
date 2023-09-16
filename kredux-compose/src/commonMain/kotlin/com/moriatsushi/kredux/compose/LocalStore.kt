package com.moriatsushi.kredux.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.moriatsushi.kredux.Store

/**
 * Create a [CompositionLocal] key that can be provided using
 * [androidx.compose.runtime.CompositionLocalProvider] for a [Store].
 *
 * An example for providing a [Store]:
 * ```
 * val LocalStore = localStoreOf<Store<State, Action>>()
 *
 * @Composable
 * fun App() {
 *     val store = remember { Store(reducer) }
 *     CompositionLocalProvider(LocalStore provides store) {
 *         // content
 *     }
 * }
 * ```
 */
fun <T : Store<*, *>> localStoreOf(): ProvidableCompositionLocal<T> {
    return staticCompositionLocalOf { error("No store provided") }
}

/**
 * Extracts a value from the current state of the [Store] provided by [localStoreOf] using the
 * [selector]. Only when the value is changed, the composable will be recomposed.
 *
 * An example of a composable that displays the current count:
 * ```
 * @Composable
 * fun Counter() {
 *    val count by LocalCounterStore.select { it.count }
 *    Text(text = "Count: $count")
 * }
 * ```
 */
@Composable
fun <T : Store<S, *>, S, R> CompositionLocal<T>.select(selector: (S) -> R): State<R> {
    val state by current.state.collectAsState()
    return remember { derivedStateOf { selector(state) } }
}

/**
 * Gets a dispatch lambda from the [Store] provided by [localStoreOf].
 *
 * An example of a composable that dispatches an action:
 * ```
 * @Composable
 * fun IncrementButton() {
 *     val dispatch = LocalCounterStore.dispatch
 *     Button(onClick = { dispatch(Action.Increment) }) {
 *         Text(text = "Increment")
 *     }
 * }
 * ```
 */
val <T : Store<*, Action>, Action> CompositionLocal<T>.dispatch: (Action) -> Unit
    @Composable
    get() {
        val store = current
        return remember { { action -> store.dispatch(action) } }
    }
