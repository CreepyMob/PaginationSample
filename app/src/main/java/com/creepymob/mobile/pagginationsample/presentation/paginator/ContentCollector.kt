package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 1:21
 *
 */
class ContentCollector<T>(private val collection: MutableCollection<T> = arrayListOf()): ContentStore<T>{

    override val content: Collection<T> = collection

    private val contentSubject : BehaviorSubject<Collection<T>>  = BehaviorSubject.createDefault(collection)

    val contentObservable: Observable<Collection<T>> = contentSubject.hide()

    fun set(newContent: Collection<T>) {
        collection.clear()
        collection.addAll(newContent)
        contentSubject.onNext(collection)
    }

    fun clear() {
        collection.clear()
        contentSubject.onNext(collection)
    }
}