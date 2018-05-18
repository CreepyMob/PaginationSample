package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * User: andrey
 * Date: 18.05.2018
 * Time: 22:43
 *
 */
class MviPaginationController<T>(private val paginationController: PaginationController<T>,
                                 private val disposable: CompositeDisposable = CompositeDisposable()) {


    val viewStateObservable: Observable<ViewState<T>>
        get() = paginationController.viewStateObservable

    fun init(observable: Observable<List<T>>, request: (Int) -> Single<out Collection<T>>) {
        paginationController.init(observable, request)
        paginationController.restart()
    }

    fun setLoadMoreEvent(loadMoreEvent: Observable<Unit>) {
        loadMoreEvent.subscribeBy {
            paginationController.loadNewPage()
        }.addTo(disposable)
    }

    fun setRefreshEvent(refreshEvent: Observable<Unit>) {
        refreshEvent.subscribeBy {
            paginationController.refresh()
        }.addTo(disposable)
    }

    fun setReloadPageEvent(reloadPageEvent: Observable<Unit>) {
        reloadPageEvent.subscribeBy {
            paginationController.retry()
        }.addTo(disposable)
    }

    fun setHardReloadEvent(hardReloadEvent: Observable<Unit>) {
        hardReloadEvent.subscribeBy {
            paginationController.restart()
        }.addTo(disposable)
    }

    fun release() {
        disposable.clear()
        paginationController.release()
    }
}