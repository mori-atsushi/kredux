package com.moriatsushi.kredux.internal

import com.moriatsushi.kredux.Middleware
import com.moriatsushi.kredux.MiddlewareScope

internal class CombinedMiddleware<State, Action : Any>(
    private val middlewares: List<Middleware<State, Action>>,
) : Middleware<State, Action> {
    override fun MiddlewareScope<State, Action>.process(action: Action): State {
        val parentScope = this
        return middlewares.foldRight({ it: Action -> next(it) }) { middleware, acc ->
            { action: Action ->
                with(middleware) { ChildMiddlewareScope(parentScope, acc).process(action) }
            }
        }.invoke(action)
    }

    private class ChildMiddlewareScope<State, Action : Any>(
        parentScope: MiddlewareScope<State, Action>,
        private val _next: (action: Action) -> State,
    ) : MiddlewareScope<State, Action> by parentScope {
        override fun next(action: Action): State {
            return _next(action)
        }
    }
}
