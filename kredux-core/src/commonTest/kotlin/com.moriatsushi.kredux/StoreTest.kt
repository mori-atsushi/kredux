package com.moriatsushi.kredux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTest {
    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        val subject = Store(
            reducer = testReducer,
            coroutineScope = backgroundScope,
        )
        assertEquals(subject.state.value.count, 0)

        subject.dispatch(TestAction.Increment)
        assertEquals(subject.state.value.count, 1)
    }

    @Test
    fun testWithInitialState() = runTest(UnconfinedTestDispatcher()) {
        val subject = Store(
            reducer = testReducer,
            initialState = TestState(100),
            coroutineScope = backgroundScope,
        )
        assertEquals(subject.state.value.count, 100)

        subject.dispatch(TestAction.Increment)
        assertEquals(subject.state.value.count, 101)
    }

    private data class TestState(val count: Int = 0)

    private sealed interface TestAction {
        object Increment : TestAction
    }

    private val testReducer: Reducer<TestState, TestAction> =
        Reducer(TestState()) { acc, action ->
            when (action) {
                TestAction.Increment -> acc.copy(count = acc.count + 1)
            }
        }
}
