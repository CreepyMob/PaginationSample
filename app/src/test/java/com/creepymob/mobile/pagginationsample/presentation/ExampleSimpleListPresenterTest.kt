package com.creepymob.mobile.pagginationsample.presentation

import com.creepymob.mobile.pagginationsample.app.RegularMviListView
import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.creepymob.mobile.pagginationsample.presentation.paginator.PaginationController
import com.creepymob.mobile.pagginationsample.presentation.paginator.ViewState
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
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
    @Mock private lateinit var paginationController: PaginationController<LoadItem>
    @Mock private lateinit var repository: DataRepository
    @Mock private lateinit var schedulersProvider: SchedulersProvider
    @Mock private lateinit var disposable: CompositeDisposable
    @Mock private lateinit var view: RegularMviListView<LoadItem>

    private val filterEventSubject = PublishSubject.create<DataLoadFilter>()
    private val loadMoreEventSubject = PublishSubject.create<Unit>()
    private val hardReloadEventSubject = PublishSubject.create<Unit>()
    private val refreshEventSubject = PublishSubject.create<Unit>()
    private val reloadPageEventSubject = PublishSubject.create<Unit>()
    @Spy private val viewStateObservableSubject = PublishSubject.create<ViewState<LoadItem>>()

    @Before
    fun setUp() {
        whenever(view.filterEvent).thenReturn(filterEventSubject)
        whenever(view.loadMoreEvent).thenReturn(loadMoreEventSubject)
        whenever(view.hardReloadEvent).thenReturn(hardReloadEventSubject)
        whenever(view.refreshEvent).thenReturn(refreshEventSubject)
        whenever(view.reloadPageEvent).thenReturn(reloadPageEventSubject)

        target = ExampleSimpleListPresenter(filter, paginationController, repository, schedulersProvider, disposable)
        target.view = view

        whenever(paginationController.viewStateObservable).thenReturn(viewStateObservableSubject)

        verify(disposable).clear()
    }

    @Test
    fun `dispose whenever setView`(){
        reset(disposable)
        target = ExampleSimpleListPresenter(filter, paginationController, repository, schedulersProvider, disposable)
        target.view = view

        verify(disposable).clear()
    }

    @Test
    fun `call loadMore when view loadMoreEvent emit event`() {
        loadMoreEventSubject.onNext(Unit)

        verify(paginationController).loadNewPage()
    }

    @Test
    fun `call restart when view hardReloadEventSubject emit event`() {
        hardReloadEventSubject.onNext(Unit)

        verify(paginationController).restart()
    }

    @Test
    fun `call refresh when view refreshEventSubject emit event`() {
        refreshEventSubject.onNext(Unit)

        verify(paginationController).refresh()
    }

    @Test
    fun `call restart when view filterEventSubject emit event`() {
        val newFilter  =mock<DataLoadFilter>()

        filterEventSubject.onNext(newFilter)

        verify(paginationController).restart()
    }

    @Test
    fun `call retry when view reloadPageEventSubject emit event`() {
        reloadPageEventSubject.onNext(Unit)

        verify(paginationController).retry()
    }

    @Test
    fun `when call init, init and restart paginationController`() {
        target.init()
        verify(paginationController).init(eq(repository.observable), any())
        verify(paginationController).restart()
    }

    @Test
    fun `when paginationController loadLambda invoke call repository update`() {
        target.init()

        val captor = argumentCaptor<(Int) -> Single<out Collection<LoadItem>>>()
        verify(paginationController).init(eq(repository.observable), captor.capture())

        captor.firstValue.invoke(0)

        verify(repository).update(filter, 0)
    }

    @Test
    fun `init presenter render viewState after viewStateObservable emit`() {
        target.init()

        val viewState = mock<ViewState<LoadItem>>()
        viewStateObservableSubject.onNext(viewState)

        verify(view).render(viewState)
    }

}