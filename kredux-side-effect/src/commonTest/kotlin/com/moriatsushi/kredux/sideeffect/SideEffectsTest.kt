package com.moriatsushi.kredux.sideeffect

import com.moriatsushi.kredux.Reducer
import com.moriatsushi.kredux.Store
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SideEffectsTest {
    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        val store = Store(
            reducer = testReducer,
            coroutineScope = backgroundScope,
            middlewares = listOf(sideEffects),
        )
        assertEquals("Initial", store.state.value)

        store.dispatch(TestAction.Request)
        assertEquals("Request", store.state.value)

        advanceTimeBy(500)
        runCurrent()
        assertEquals("Result(success)", store.state.value)
    }

    private val sideEffects = sideEffects<String, TestAction> {
        collect<TestAction.Request> {
            delay(500)
            dispatch(TestAction.Result("success"))
        }
    }

    private val testReducer = Reducer("Initial") { _, action: TestAction ->
        when (action) {
            is TestAction.Request -> "Request"
            is TestAction.Result -> "Result(${action.value})"
        }
    }

    private sealed interface TestAction {
        object Request : TestAction
        data class Result(val value: String) : TestAction
    }
}
