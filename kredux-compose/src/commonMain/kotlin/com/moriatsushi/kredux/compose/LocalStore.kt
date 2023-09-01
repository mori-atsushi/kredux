package com.moriatsushi.kredux.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.moriatsushi.kredux.Store

interface LocalStore<T : Store<*, *>> {
    @get:Composable
    val current: T

    infix fun provides(value: T): ProvidedValue<T>
}

fun <T : Store<*, *>> localStoreOf(): LocalStore<T> {
    return LocalStoreImpl()
}

private class LocalStoreImpl<T : Store<*, *>> : LocalStore<T> {
    override val current: T
        @Composable
        get() = local.current

    override fun provides(value: T): ProvidedValue<T> {
        return local provides value
    }

    private val local = staticCompositionLocalOf<T> {
        error("No store provided")
    }
}

@Composable
fun <T : Store<S, *>, S, R> LocalStore<T>.select(selector: (S) -> R): State<R> {
    val state by current.state.collectAsState()
    return remember { derivedStateOf { selector(state) } }
}

val <T : Store<*, Action>, Action> LocalStore<T>.dispatch: (Action) -> Unit
    @Composable
    get() {
        val store = current
        return remember { { action -> store.dispatch(action) } }
    }
