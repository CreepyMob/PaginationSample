package com.creepymob.mobile.pagginationsample.presentation

import com.creepymob.mobile.pagginationsample.app.RegularMviListView
import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.paginator.MviPaginationController
import com.creepymob.mobile.pagginationsample.presentation.paginator.ViewState
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 12.05.2018
 * Time: 19:33
 */
@RunWith(MockitoJUnitRunner::class)
class ExampleSimpleListPresenterTest {

    private lateinit var target: ExampleSimpleListPresenter
    @Mock private lateinit var filter: DataLoadFilter
    @Mock private lateinit var mviPaginationController: MviPaginationController<LoadItem>
    @Mock private lateinit var repository: DataRepository
    @Mock private lateinit var schedulersProvider: SchedulersProvider
    @Mock private lateinit var disposable: CompositeDisposable
    @Mock private lateinit var view: RegularMviListView<LoadItem>

    @Spy private val viewStateObservableSubject = PublishSubject.create<ViewState<LoadItem>>()

    @Before
    fun setUp() {
        target = ExampleSimpleListPresenter(filter, mviPaginationController, repository, schedulersProvider, disposable)
        whenever(mviPaginationController.viewStateObservable).thenReturn(viewStateObservableSubject)
    }

    @Test
    fun `setViewEvent to mviController when setView non null View`() {
        whenever(view.reloadPageEvent).thenReturn(mock())
        whenever(view.refreshEvent).thenReturn(mock())
        whenever(view.hardReloadEvent).thenReturn(mock())
        whenever(view.loadMoreEvent).thenReturn(mock())

        target.view = view

        verify(mviPaginationController).setReloadPageEvent(view.reloadPageEvent)
        verify(mviPaginationController).setRefreshEvent(view.refreshEvent)
        verify(mviPaginationController).setHardReloadEvent(view.hardReloadEvent)
        verify(mviPaginationController).setLoadMoreEvent(view.loadMoreEvent)

    }

    @Test
    fun `setViewEvent to mviController when setView nullable View`() {
        target.view = null

        verify(disposable).clear()
        verify(mviPaginationController).release()
    }


    @Test
    fun `init presenter render viewState after viewStateObservable emit`() {
        target.view = view
        target.init()

        val viewState = mock<ViewState<LoadItem>>()
        viewStateObservableSubject.onNext(viewState)

        verify(view).render(viewState)
    }

}