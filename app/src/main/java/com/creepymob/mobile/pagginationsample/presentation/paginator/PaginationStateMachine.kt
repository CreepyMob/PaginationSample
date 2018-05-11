package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class StateInvoker<T>(private val viewSateFactory: ViewStateFactory<T>) {

    private val viewStateSubject: PublishSubject<ViewState<T>> = PublishSubject.create()

    val viewStateObservable: Observable<ViewState<T>> = viewStateSubject.hide()

    operator fun invoke(previousState: State<T>,
                        newState: State<T>,
                        loader: PageContentLoader<T>,
                        cacheDataObserver: CacheDataObserver<T>) {
        System.out.println("StateInvoker previous: ${previousState::class.simpleName}, " +
                "cur: ${newState::class.simpleName} " +
                "is not same: ${previousState !== newState}")
        if (previousState !== newState) {

            viewSateFactory.create(newState)?.also {
                System.out.println("ViewSateFactory create viewState: ${it::class.simpleName}")
                viewStateSubject.onNext(it)
            }

            newState.invoke(loader, cacheDataObserver)
        }
    }
}

class StateStore<T>(var state: State<T> = InitialState())

class PaginationStateMachine<T>(
        private val stateStore: StateStore<T>,
        private val stateApplier: StateApplier<T>) {

    fun restart() {
        stateApplier.apply(stateStore.state.restart())
    }

    fun refresh() {
        stateApplier.apply(stateStore.state.refresh())
    }

    fun retry() {
        stateApplier.apply(stateStore.state.retry())
    }

    fun loadNewPage() {
        stateApplier.apply(stateStore.state.loadNewPage())
    }

    fun updateCache(emptyCache: Boolean) {
        stateApplier.apply(stateStore.state.updateCache(emptyCache))
    }

    fun newPage(emptyPage: Boolean) {
        stateApplier.apply(stateStore.state.newPage(emptyPage))
    }

    fun fail(throwable: Throwable) {
        stateApplier.apply(stateStore.state.fail(throwable))
    }

    fun release() {
        stateApplier.apply(stateStore.state.release())
    }

}
