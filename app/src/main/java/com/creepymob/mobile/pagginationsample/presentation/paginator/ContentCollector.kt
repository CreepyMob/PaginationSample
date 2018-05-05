package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 1:21
 *
 */
class ContentCollector<T>(private val collection: MutableCollection<T> = arrayListOf()) {

    val content: Collection<T>
        get() = collection

    fun add(newContent: Collection<T>) {
        collection.clear()
        collection.addAll(newContent)
    }

    fun clear() {
        collection.clear()
    }
}