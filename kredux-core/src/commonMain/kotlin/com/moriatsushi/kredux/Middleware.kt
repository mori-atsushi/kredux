package com.moriatsushi.kredux

import kotlinx.coroutines.CoroutineScope

/**
 * A middleware that can wrap the [Store.dispatch] method.
 *
 * An example of a middleware that logs actions and states:
 * ```
 * class LoggerMiddleware<State, Action : Any> : Middleware<State, Action> {
 *    override fun MiddlewareScope<State, Action>.process(action: Action): State {
 *        println("action: $action")
 *        println("current state: $state")
 *        val nextState = next(action)
 *        println("next state: $nextState")
 *        return nextState
 *    }
 * }
 * ```
 */
fun interface Middleware<State, Action : Any> {
    /**
     * Processes an [action] and returns the next state. This function is given the
     * [next][MiddlewareScope.next] function that is the next middleware function in the chain,
     * and is expected to call `next(action)` with different arguments, or at different times,
     * or maybe not call it at all. The last middleware in the chain will receive the real
     * [Store.dispatch] function as the [next][MiddlewareScope.next], thus ending the chain.
     */
    fun MiddlewareScope<State, Action>.process(action: Action): State
}

/**
 * A scope for a [Middleware].
 *
 * **Do not implement this interface directly.** New methods or properties might be added to this
 * interface in the future.
 */
interface MiddlewareScope<State, Action : Any> {
    /**
     * The current state of the [Store].
     */
    val state: State

    /**
     * The [CoroutineScope] of the [Store].
     */
    val coroutineScope: CoroutineScope

    /**
     * Calls the next middleware function in the chain, or the real [Store.dispatch] function if the
     * middleware is the last in the chain.
     */
    fun next(action: Action): State

    /**
     * Dispatches an [action]. It is the same as calling [Store.dispatch].
     */
    fun dispatch(action: Action)
}
