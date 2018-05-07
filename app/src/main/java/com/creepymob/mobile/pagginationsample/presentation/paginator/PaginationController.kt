package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.creepymob.mobile.pagginationsample.app.SchedulersProviderImpl
import io.reactivex.Observable
import io.reactivex.Single

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 18:32
 */
class PaginationController<T>(
        private val stateInvoker: StateInvoker<T>,
        private val stateMachine: PaginationStateMachine<T>,
        private val pageContentLoader: PageContentLoader<T>,
        private val cacheDataObserver: CacheDataObserver<T>) {

    companion object {

        fun <T> default(): PaginationController<T> {
            val schedulersProvider = SchedulersProviderImpl()
            val stateInvoker = StateInvoker<T>()
            val stateStore = StateStore<T>()
            val contentCollector = ContentCollector<T>()
            val pageContentLoader = PageContentLoader<T>(contentCollector, schedulersProvider)
            val cacheDataObserver = CacheDataObserver(contentCollector)

            val stateApplier = StateApplier<T>(pageContentLoader, stateStore, contentCollector, cacheDataObserver, stateInvoker)

            val stateMachine = PaginationStateMachine<T>(stateStore, stateApplier)


            return PaginationController(stateInvoker, stateMachine, pageContentLoader, cacheDataObserver)
        }
    }

    val viewStateObservable: Observable<ViewState<T>>
        get() = stateInvoker.viewStateObservable

    fun init(request: (Int) -> Single<out Collection<T>>, observable: Observable<List<T>>) {
        pageContentLoader.init(request, stateMachine)
        cacheDataObserver.init(observable, stateMachine)
    }

    fun restart() {
        stateMachine.restart()
    }

    fun refresh() {
        stateMachine.refresh()
    }

    fun retry() {
        stateMachine.retry()
    }

    fun loadNewPage() {
        stateMachine.loadNewPage()
    }

    fun release() {
        stateMachine.release()
    }
}
