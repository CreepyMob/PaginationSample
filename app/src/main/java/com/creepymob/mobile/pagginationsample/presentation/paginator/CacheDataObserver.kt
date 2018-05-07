package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 21:25
 *
 */
class CacheDataObserver<T>(private val contentCollector: ContentCollector<T>,
                           private val disposable: CompositeDisposable = CompositeDisposable()) {

    fun init(observable: Observable<List<T>>, stateMachine: PaginationStateMachine<T>) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    contentCollector.set(it)
                    stateMachine.updateCache(it.isEmpty())
                }
    }

    fun release() {
        disposable.dispose()
    }
}