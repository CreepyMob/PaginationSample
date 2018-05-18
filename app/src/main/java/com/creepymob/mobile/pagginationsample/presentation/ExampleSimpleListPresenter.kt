package com.creepymob.mobile.pagginationsample.presentation

import com.creepymob.mobile.pagginationsample.app.RegularMviListView
import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.paginator.MviPaginationController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 0:08
 */
class ExampleSimpleListPresenter(private val filter: DataLoadFilter,
                                 private val mviPaginationController: MviPaginationController<LoadItem>,
                                 private val repository: DataRepository,
                                 private val schedulersProvider: SchedulersProvider,
                                 private val disposable: CompositeDisposable) {


    var view: RegularMviListView<LoadItem>? = null
        set(value) {
            field = value

            value?.apply {
                mviPaginationController.setLoadMoreEvent(loadMoreEvent)
                mviPaginationController.setRefreshEvent(refreshEvent)
                mviPaginationController.setReloadPageEvent(reloadPageEvent)
                mviPaginationController.setHardReloadEvent(hardReloadEvent)
            }

            if (view == null) {
                mviPaginationController.release()
                disposable.clear()
            }
        }

    fun init() {

        mviPaginationController.viewStateObservable
                .subscribeBy {
                    view?.render(it)
                }.addTo(disposable)

        mviPaginationController.init(repository.observable,
                { page: Int -> repository.update(filter, page) })
    }
}

