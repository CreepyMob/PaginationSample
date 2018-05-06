package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Observable
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

class StateStore<T>(var state: State<T> = InitialProgress<T>())

class PaginationStateMachine<T>(/*private val stateInvoker: StateInvoker<T> = StateInvoker(),
                                private val pageContentLoader: PageContentLoader<T> = PageContentLoader(),*/
        private val stateStore: StateStore<T>,
        private val stateApplier: StateApplier<T>) {

    /*  val viewStateObservable: Observable<ViewState<T>>
          get() = stateInvoker.viewStateObservable


      fun init(request: (Int) -> Single<out Collection<T>>) {
          pageContentLoader.init(request, this)
          stateApplier.apply(stateStore.state)
      }*/

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
