package org.wspcgir.strong_giraffe.util

sealed class Maybe<T> {

    abstract fun <S> over(action: (T) -> S): Maybe<S>

    abstract fun toNull(): T?

    class Nothing<T> : Maybe<T>() {
        override fun <S> over(action: (T) -> S): Maybe<S> {
            return Nothing()
        }

        override fun toNull(): T? {
            return null
        }
    }

    class Just<T>(val some: T) : Maybe<T>() {
        override fun <S> over(action: (T) -> S): Maybe<S> {
            return Just(action.invoke(some))
        }

        override fun toNull(): T? {
            return some
        }
    }

    companion object {
        fun <T> fromNull(value: T?): Maybe<T> {
            return if (value == null) {
                Nothing()
            } else {
                Just(value)
            }
        }
    }
}