package com.moriatsushi.kredux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MiddlewareTest {
    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        val store = Store(
            reducer = testReducer,
            middlewares = listOf(
                NamedMiddleware("middleware1"),
                NamedMiddleware("middleware2"),
                NamedMiddleware("middleware3"),
            ),
            coroutineScope = backgroundScope,
        )
        store.dispatch("action")
        val expected = "middleware1(middleware2(middleware3(state)))"
        assertEquals(expected, store.state.value)
    }

    private val testReducer = Reducer("initial") { acc, action: String ->
        when (action) {
            "action" -> "state"
            else -> acc
        }
    }

    private class NamedMiddleware(private val name: String) : Middleware<String, String> {
        override fun MiddlewareScope<String, String>.process(action: String): String {
            return "$name(${next(action)})"
        }
    }
}
