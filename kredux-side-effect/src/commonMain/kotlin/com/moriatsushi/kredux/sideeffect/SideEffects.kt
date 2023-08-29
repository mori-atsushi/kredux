package com.moriatsushi.kredux.sideeffect

import com.moriatsushi.kredux.Middleware
import com.moriatsushi.kredux.MiddlewareScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <State, Action : Any> sideEffects(
    builder: SideEffectsBuilder<State, Action>.() -> Unit
): Middleware<State, Action> {
    val sideEffectBuilder = SideEffectsBuilder<State, Action>()
    sideEffectBuilder.builder()
    return SideEffectsMiddleware(sideEffectBuilder.build())
}

class SideEffectsBuilder<State, Action : Any> internal constructor() {
    private val sideEffects = mutableListOf<SideEffect<State, Action>>()

    inline fun <reified T : Action> collect(
        crossinline block: suspend SideEffectScope<State, Action>.(action: T) -> Unit
    ) {
        collect(canHandle = { it is T }) {
            block(it as T)
        }
    }

    inline fun <reified T : Action> collect(
        target: T,
        crossinline block: suspend SideEffectScope<State, Action>.(action: T) -> Unit
    ) {
        collect(canHandle = { it == target }) {
            block(it as T)
        }
    }

    @PublishedApi
    internal fun collect(
        canHandle: (Action) -> Boolean,
        block: suspend SideEffectScope<State, Action>.(Action) -> Unit
    ) {
        sideEffects.add(SideEffect(canHandle, block))
    }

    internal fun build(): List<SideEffect<State, Action>> {
        return sideEffects
    }
}

private class SideEffectsMiddleware<State, Action : Any>(
    private val sideEffects: List<SideEffect<State, Action>>,
) : Middleware<State, Action> {
    override fun MiddlewareScope<State, Action>.process(action: Action): State {
        val sideEffectScope = SideEffectScopeImpl(this)

        sideEffects.forEach { sideEffect ->
            if (sideEffect.canHandle(action)) {
                coroutineScope.launch {
                    with(sideEffect) { sideEffectScope.block(action) }
                }
            }
        }

        return next(action)
    }
}

internal class SideEffect<State, Action : Any>(
    val canHandle: (Action) -> Boolean,
    val block: suspend SideEffectScope<State, Action>.(Action) -> Unit,
)

interface SideEffectScope<State, Action : Any> : CoroutineScope {
    val state: State
    fun dispatch(action: Action)
}

private class SideEffectScopeImpl<State, Action : Any>(
    private val middlewareScope: MiddlewareScope<State, Action>,
) : SideEffectScope<State, Action>, CoroutineScope by middlewareScope.coroutineScope {
    override val state: State get() = middlewareScope.state

    override fun dispatch(action: Action) {
        middlewareScope.dispatch(action)
    }
}
