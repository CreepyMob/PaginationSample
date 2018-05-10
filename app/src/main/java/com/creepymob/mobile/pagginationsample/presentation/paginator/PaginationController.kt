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
            val contentCollector = ContentCollector<T>()
            val viewStateFactory = ViewStateFactory(contentCollector)
            val stateInvoker = StateInvoker(viewStateFactory)
            val stateStore = StateStore<T>()

            val pageContentLoader = PageContentLoader(contentCollector, schedulersProvider)
            val cacheDataObserver = CacheDataObserver(contentCollector)

            val stateApplier = StateApplier(pageContentLoader, stateStore, cacheDataObserver, stateInvoker)

            val stateMachine = PaginationStateMachine(stateStore, stateApplier)


            return PaginationController(stateInvoker, stateMachine, pageContentLoader, cacheDataObserver)
        }
    }

    val viewStateObservable: Observable<ViewState<T>>
        get() = stateInvoker.viewStateObservable

    fun init(observable: Observable<List<T>>, request: (Int) -> Single<out Collection<T>>) {
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
