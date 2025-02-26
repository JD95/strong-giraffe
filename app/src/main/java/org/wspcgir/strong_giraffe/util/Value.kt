package org.wspcgir.strong_giraffe.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class Value<Parent>(val state: MutableState<Parent>) {

    private var listeners: List<(Parent) -> Unit> = emptyList()

    private fun register(action: (Parent) -> Unit) {
        listeners = listeners.plus(action)
    }

    fun modify(action: (Parent) -> Parent) {
        set(action(state.value))
    }

    fun set(value: Parent) {
        if (state.value != value) {
            state.value = value
            for (action in listeners) {
                action.invoke(value)
            }
        }
    }

    fun <Child> child(
        get: (Parent) -> Child,
        update: (Parent, Child) -> Parent
    ): Value<Child> {
        val child = Value(mutableStateOf(get(state.value)))
        this.register { child.set(get(state.value)) }
        child.register { this.set(update(state.value, it)) }
        return child
    }
}