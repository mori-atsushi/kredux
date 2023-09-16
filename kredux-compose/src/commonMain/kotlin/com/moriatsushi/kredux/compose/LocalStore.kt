package com.moriatsushi.kredux.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.moriatsushi.kredux.Store

fun <T : Store<*, *>> localStoreOf(): ProvidableCompositionLocal<T> {
    return staticCompositionLocalOf { error("No store provided") }
}

@Composable
fun <T : Store<S, *>, S, R> ProvidableCompositionLocal<T>.select(selector: (S) -> R): State<R> {
    val state by current.state.collectAsState()
    return remember { derivedStateOf { selector(state) } }
}

val <T : Store<*, Action>, Action> ProvidableCompositionLocal<T>.dispatch: (Action) -> Unit
    @Composable
    get() {
        val store = current
        return remember { { action -> store.dispatch(action) } }
    }
