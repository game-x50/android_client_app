package com.ruslan.hlushan.extensions

import kotlin.math.max

/**
 * @author Ruslan Hlushan on 12/27/18
 */

inline fun <T : Any?> T.isIn(vararg objects: T): Boolean = objects.contains(this)

inline fun <T> MutableList<T>.removeLast(): T? = if (isNotEmpty()) removeAt(lastIndex) else null

inline fun <T> MutableList<T>.removeCountAndGet(count: Int): List<T> {
    val returnList = this.take(count)

    this.clearAndAddAll(this.takeLast(max(this.size - count, 0)))

    return returnList
}

inline fun <T> MutableCollection<T>.clearAndAddAll(newValues: Iterable<T>): Boolean {
    clear()
    return addAll(newValues)
}

fun <E> MutableCollection<E>.removeFirst(predicate: (E) -> Boolean): E? {
    val element = firstOrNull(predicate)
    if (element != null) {
        remove(element)
    }
    return element
}

fun <E> MutableCollection<E>.removeLast(predicate: (E) -> Boolean): E? {
    val element = lastOrNull(predicate)
    if (element != null) {
        remove(element)
    }
    return element
}

fun <E> E.addAsFirstTo(list: List<E>): List<E> {
    val result = ArrayList<E>(list.size + 1)
    result.add(this)
    result.addAll(list)
    return result
}

inline fun <E : Any, T : Collection<E>> T?.withNotNullNorEmpty(func: T.() -> Unit) {
    if (this != null && this.isNotEmpty()) {
        with(this) { func() }
    }
}

inline fun <E : Any, T : Collection<E>> T?.whenNotNullNorEmpty(func: (T) -> Unit,
                                                               nullFunc: () -> Unit) {
    if (this != null && this.isNotEmpty()) {
        func(this)
    } else {
        nullFunc()
    }
}

inline fun <T : Any?> Collection<T>.notContains(element: T): Boolean = !this.contains(element)

inline fun <T : Any?> List<T>.indexOfOrNull(item: T): Int? =
        this.indexOf(item).takeIf { index -> index in this.indices }

inline fun <T : Any?> List<T>.copy(): List<T> = this.toList()

inline fun <T : Any?> MutableList<T>.replace(old: T, new: T) {
    val index = this.indexOf(old)
    if (index in this.indices) {
        this[index] = new
    }
}

inline fun <E> Collection<E>.indexOfFirstOrNull(predicate: (E) -> Boolean): Int? =
        this.indexOfFirst(predicate).takeIf { it >= 0 }

inline fun <E> Collection<E>.indexOfFirstOrElse(defaultValue: Int, predicate: (E) -> Boolean): Int =
        this.indexOfFirst(predicate).takeIf { it >= 0 } ?: defaultValue

inline fun <T : Any?> Collection<T>.withoutFirst(predicate: (T) -> Boolean): List<T> {
    val itemToRemoveIndex = this.indexOfFirstOrNull(predicate)
    val result = ArrayList<T>(this)
    if (itemToRemoveIndex != null) {
        result.removeAt(itemToRemoveIndex)
    }
    return result
}

inline fun <T : Any?> Collection<T>.withReplacedFirst(new: T, predicate: (T) -> Boolean): List<T> {
    val itemToReplaceIndex = this.indexOfFirstOrNull(predicate)
    val result = ArrayList<T>(this)
    if (itemToReplaceIndex != null) {
        result[itemToReplaceIndex] = new
    }
    return result
}