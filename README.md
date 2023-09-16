# KRedux
KRedux is a simple implementation of Redux in Kotlin.

## Setup
```kotlin
dependencies {
    implementation("com.moriatsushi.kredux:kredux-core:1.0.0-alpha01")
    // optional: If you want to use side effect.
    implementation("com.moriatsushi.kredux:kredux-side-effect:1.0.0-alpha01")
    // optional: If you want to use this with Jetpack Compose.
    implementation("com.moriatsushi.kredux:kredux-compose:1.0.0-alpha01")
}
```

## Usage
**Step 1. Define State and Action**

```kotlin
data class CounterState(val count: Int = 0)

sealed class CounterAction {
    data object Increment : CounterAction()
    data object Decrement : CounterAction()
}
```

**Step 2. Define Reducer**

```kotlin
val counterReducer = Reducer(CounterState(0)) { acc, action: CounterAction ->
    when (action) {
        is CounterAction.Increment -> acc.copy(count = state.count + 1)
        is CounterAction.Decrement -> acc.copy(count = state.count - 1)
    }
}
```

**Step 3. Create Store**

```kotlin
val store = Store(counterReducer)
```

## APIs
https://mori-atsushi.github.io/kredux/
