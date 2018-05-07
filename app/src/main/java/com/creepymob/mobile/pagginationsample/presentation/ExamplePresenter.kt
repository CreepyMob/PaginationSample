package com.creepymob.mobile.pagginationsample.presentation

import com.creepymob.mobile.pagginationsample.app.RegularMviListView
import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.paginator.PaginationController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 0:08
 */
class ExamplePresenter(private val filter: DataLoadFilter,
                       private val paginationController: PaginationController<LoadItem>,
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
                    paginationController.loadNewPage()
                }.addTo(disposable)

                refreshEvent.subscribeBy {
                    paginationController.refresh()
                }.addTo(disposable)

                filterEvent.subscribeBy {
                    paginationController.restart()
                }.addTo(disposable)

                reloadPageEvent.subscribeBy {
                    paginationController.retry()
                }.addTo(disposable)

                hardReloadEvent.subscribeBy {
                    paginationController.restart()
                }.addTo(disposable)
            }


            if (view == null) {
                paginationController.release()
            }
        }

    fun init() {

        paginationController.viewStateObservable
                .subscribeBy {
                    view?.render(it)
                }.addTo(disposable)

        paginationController.init( repository.observable,
                { page: Int -> repository.update(filter, page) })

        paginationController.refresh()

    }
}

