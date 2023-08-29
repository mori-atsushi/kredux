package com.moriatsushi.kredux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTest {
    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        val subject = testStore(backgroundScope)
        assertEquals(subject.state.value.count, 0)

        subject.dispatch(TestAction.Increment)
        assertEquals(subject.state.value.count, 1)
    }

    private fun testStore(coroutineScope: CoroutineScope): Store<TestState, TestAction> =
        createStore(
            reducer = testReducer,
            coroutineScope = coroutineScope,
        )

    private data class TestState(val count: Int = 0)

    private sealed interface TestAction {
        object Increment : TestAction
    }

    private val testReducer: Reducer<TestState, TestAction> =
        createReducer(TestState()) { acc, action ->
            when (action) {
                TestAction.Increment -> acc.copy(count = acc.count + 1)
            }
        }
}
