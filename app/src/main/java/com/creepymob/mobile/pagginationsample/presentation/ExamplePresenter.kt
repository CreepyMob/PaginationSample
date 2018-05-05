package com.creepymob.mobile.pagginationsample.presentation

import com.creepymob.mobile.pagginationsample.app.RegularMviListView
import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.paginator.PaginationStateMachine
import com.creepymob.mobile.pagginationsample.presentation.paginator.ViewState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 0:08
 */
class ExamplePresenter(private val filter: DataLoadFilter,
                       private val paginationStateMachine: PaginationStateMachine<LoadItem>,
                       private val repository: DataRepository,
                       private val schedulersProvider: SchedulersProvider,
                       private val disposable: CompositeDisposable
) {


    var view: RegularMviListView<LoadItem>? = null
        set(value) {
            field = value
            disposable.clear()

            value?.apply {
                loadMoreEvent.subscribeBy {
                    paginationStateMachine.loadNewPage()
                }.addTo(disposable)

                refreshEvent.subscribeBy {
                    paginationStateMachine.refresh()
                }.addTo(disposable)

                filterEvent.subscribeBy {
                    paginationStateMachine.restart()
                }.addTo(disposable)

                reloadPageEvent.subscribeBy {
                    paginationStateMachine.retry()
                }.addTo(disposable)
            }


            if (view == null) {
                paginationStateMachine.release()
            }
        }

    fun init() {

        Observables.combineLatest(
                paginationStateMachine.viewStateObservable,
                repository.observable
        )
                .observeOn(schedulersProvider.main())
                .map {
                    if (it.first is ViewState.ContentViewState<LoadItem>) {
                        (it.first as ViewState.ContentViewState<LoadItem>).copy(content = it.second)
                    } else {
                        it.first
                    }
                }.subscribeBy {
                    view?.render(it)
                }.addTo(disposable)

        paginationStateMachine.init(
                { page: Int ->
                    repository.update(filter, page)
                            .andThen(repository.observable.firstOrError())
                            .subscribeOn(schedulersProvider.io())
                            .observeOn(schedulersProvider.main())
                })
        paginationStateMachine.refresh()

    }
}

