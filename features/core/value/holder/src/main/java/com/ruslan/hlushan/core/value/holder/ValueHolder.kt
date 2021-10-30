package com.ruslan.hlushan.core.value.holder

interface ValueHolder<out T : Any?> {
    val value: T
}

interface MutableValueHolder<T : Any?> : ValueHolder<T> {
    override var value: T
}

@SuppressWarnings("FunctionNaming")
fun <T : Any?> ValueHolder(value: T): ValueHolder<T> = ValueHolderImpl(value)

@SuppressWarnings("FunctionNaming")
fun <T : Any?> MutableValueHolder(value: T): MutableValueHolder<T> = MutableValueHolderImpl(value)

private data class ValueHolderImpl<out T : Any?>(override val value: T) : ValueHolder<T>

@SuppressWarnings("DataClassShouldBeImmutable")
private data class MutableValueHolderImpl<T : Any?>(override var value: T) : MutableValueHolder<T>