package com.moriatsushi.kredux

import kotlin.test.Test
import kotlin.test.assertEquals

class CombineReducersTest {
    @Test
    fun combineTwoReducers() {
        val childReducer1 = Reducer(0) { acc, action: String ->
            when (action) {
                "child1:add" -> acc + 1
                else -> acc
            }
        }
        val childReducer2 = Reducer(0) { acc, action: String ->
            when (action) {
                "child2:add" -> acc + 1
                else -> acc
            }
        }
        val combinedReducer = combineReducers(
            child(childReducer1) { it.first },
            child(childReducer2) { it.second },
        ) { t1, t2 ->
            t1 to t2
        }

        var current = combinedReducer.initialState
        assertEquals(0 to 0, current)

        current = combinedReducer.reduce(current, "child1:add")
        assertEquals(1 to 0, current)

        current = combinedReducer.reduce(current, "child2:add")
        assertEquals(1 to 1, current)
    }

    @Test
    fun combineSameReducers() {
        val childReducer = Reducer(0) { acc, action: String ->
            when (action) {
                "add" -> acc + 1
                else -> acc
            }
        }
        val combinedReducer = combineReducers(
            child(childReducer) { it.first },
            child(childReducer) { it.second },
        ) { t1, t2 ->
            t1 to t2
        }

        var current = combinedReducer.initialState
        assertEquals(0 to 0, current)

        current = combinedReducer.reduce(current, "add")
        assertEquals(1 to 1, current)

        current = combinedReducer.reduce(current, "add")
        assertEquals(2 to 2, current)
    }

    @Test
    fun mapToChildAction() {
        val childReducer1 = Reducer(0) { acc, action: String ->
            when (action) {
                "add" -> acc + 1
                else -> acc
            }
        }
        val childReducer2 = Reducer(0) { acc, action: String ->
            when (action) {
                "add" -> acc + 1
                else -> acc
            }
        }
        val combinedReducer = combineReducers(
            child(
                childReducer1,
                mapToChildAction = { action: String ->
                    action
                        .takeIf { it.startsWith("child1:") }
                        ?.removePrefix("child1:")
                },
            ) { it.first },
            child(
                childReducer2,
                mapToChildAction = { action: String ->
                    action
                        .takeIf { it.startsWith("child2:") }
                        ?.removePrefix("child2:")
                },
            ) { it.second },
        ) { t1, t2 ->
            t1 to t2
        }

        var current = combinedReducer.initialState
        assertEquals(0 to 0, current)

        current = combinedReducer.reduce(current, "child1:add")
        assertEquals(1 to 0, current)

        current = combinedReducer.reduce(current, "child2:add")
        assertEquals(1 to 1, current)
    }
}
