package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class StateInvoker<T> {

    private val viewStateSubject: PublishSubject<ViewState<T>> = PublishSubject.create()

    val viewStateObservable: Observable<ViewState<T>> = viewStateSubject.hide()

    operator fun invoke(state: State<T>, loader: PageContentLoader<T>) {
        System.out.println("State invoker state: ${state::class.simpleName} is invoked: ${state.invoked()}")
        if (!state.invoked()) {
            state.invoke(loader)?.also {
                System.out.println("State invoker viewState: ${it::class.simpleName}")
                viewStateSubject.onNext(it)
            }
        }
    }
}

class StateStore<T>(var state: State<T> = EmptyState<T>())

class PaginationStateMachine<T>(private val stateInvoker: StateInvoker<T> = StateInvoker(),
                                private val pageContentLoader: PageContentLoader<T> = PageContentLoader(),
                                private val stateStore: StateStore<T> = StateStore()) {

    val viewStateObservable: Observable<ViewState<T>>
        get() = stateInvoker.viewStateObservable


    fun init(request: (Int) -> Single<out Collection<T>>) {
        pageContentLoader.init(request, this)
        apply(stateStore.state)
    }

    fun restart() {
        apply(stateStore.state.restart())
    }

    fun refresh() {
        apply(stateStore.state.refresh())
    }

    fun retry() {
        apply(stateStore.state.retry())
    }

    fun loadNewPage() {
        apply(stateStore.state.loadNewPage())
    }

    fun release() {
        apply(stateStore.state.release())
    }

    fun apply(newState: State<T>) {
        stateStore.state = newState
        stateInvoker(newState, pageContentLoader)
    }


}
