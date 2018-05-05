package com.creepymob.mobile.pagginationsample.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.creepymob.mobile.pagginationsample.R
import com.creepymob.mobile.pagginationsample.app.adapter.MainAdapter
import com.creepymob.mobile.pagginationsample.app.data.DisplayableItem
import com.creepymob.mobile.pagginationsample.app.data.NewPageErrorLoading
import com.creepymob.mobile.pagginationsample.app.data.NewPageProgressLoader
import com.creepymob.mobile.pagginationsample.data.DataRepositoryImpl
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.ExamplePresenter
import com.creepymob.mobile.pagginationsample.presentation.paginator.PaginationStateMachine
import com.creepymob.mobile.pagginationsample.presentation.paginator.ViewState
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RegularMviListView<LoadItem> {

    private lateinit var presenter: ExamplePresenter
    private lateinit var adapter: MainAdapter
    private val reloadPageSubject: PublishSubject<Unit> = PublishSubject.create()
    private val onLoadItemClickSubject: PublishSubject<LoadItem> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val initialFilter = DataLoadFilter()

        adapter = MainAdapter(reloadPageSubject, onLoadItemClickSubject)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        presenter = ExamplePresenter(initialFilter,
                PaginationStateMachine(),
                DataRepositoryImpl(),
                SchedulersProviderImpl(),
                CompositeDisposable())
        presenter.view = this
        presenter.init()

        onLoadItemClickSubject.subscribe {
            Toast.makeText(this, "LoadItem click title: ${it.title}, message: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.view = null
    }

    override val loadMoreEvent: Observable<Unit> by lazy {
        RxRecyclerView
                .scrollEvents(recyclerView)
                .filter { event -> event.view().canScrollVertically(1) }
                .map { Unit }
                .doOnNext { System.out.println("RecyclerView SCROLL DOWN") }
    }

    override val refreshEvent: Observable<Unit> by lazy {
        System.out.println("SwipeRefreshLayout create refreshEvent")
        Observable.merge(RxSwipeRefreshLayout.refreshes(contentLayout)
                .doOnNext { System.out.println("SwipeRefreshLayout REFRESH") }
                .map { Unit },
                RxView.clicks(reloadErrorButton).map { Unit },
                RxView.clicks(reloadEmptyButton).map { Unit })
    }

    override val filterEvent: Observable<DataLoadFilter>  by lazy { Observable.never<DataLoadFilter>() }

    override val reloadPageEvent: Observable<Unit>  by lazy { reloadPageSubject.hide() }

    override fun render(listViewState: ViewState<LoadItem>) {
        System.out.println("ViewState render listViewState: $listViewState")
        when (listViewState) {
            is ViewState.EmptyLoadingViewState<LoadItem> -> showEmptyLoading()
            is ViewState.EmptyListErrorViewState<LoadItem> -> showEmptyListError()
            is ViewState.EmptyContentViewState<LoadItem> -> showEmptyContent()
            is ViewState.ContentViewState<LoadItem> -> showContent(listViewState)
        }
    }

    private fun showContent(it: ViewState.ContentViewState<LoadItem>) {
        System.out.println("ViewState showContent")

        contentLayout.visible = true
        progressBar.visible = false
        emptyContentLayout.visible = false
        errorContentLayout.visible = false

        contentLayout.isRefreshing = it.isRefresh
        adapter.content = getDisplayableItemsForViewState(it)
    }

    private fun showEmptyContent() {
        System.out.println("ViewState showEmptyContent")

        contentLayout.visible = false
        progressBar.visible = false
        emptyContentLayout.visible = true
        errorContentLayout.visible = false
    }

    private fun showEmptyListError() {
        System.out.println("ViewState showEmptyListError")

        contentLayout.visible = false
        progressBar.visible = false
        emptyContentLayout.visible = false
        errorContentLayout.visible = true
    }

    private fun showEmptyLoading() {
        System.out.println("ViewState showEmptyLoading")

        contentLayout.visible = false
        progressBar.visibility = View.VISIBLE
        emptyContentLayout.visible = false
        errorContentLayout.visible = false
    }

    private fun getDisplayableItemsForViewState(it: ViewState.ContentViewState<LoadItem>): List<DisplayableItem> {

        val list = mutableListOf<DisplayableItem>()
        list.addAll(it.content)

        if (it.isNextPageLoaded) {
            list.add(NewPageProgressLoader)
        }

        if (it.contentThrowable != null && it.contentThrowable.whenNextPageLoaded) {
            list.add(NewPageErrorLoading)
        }

        return list
    }
}
