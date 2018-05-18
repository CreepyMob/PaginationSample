package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 19.05.2018
 * Time: 0:01
 */
@RunWith(MockitoJUnitRunner::class)
class MviPaginationControllerTest {

    private lateinit var target: MviPaginationController<Any>
    @Mock private lateinit var paginationController: PaginationController<Any>

    private val loadMoreEventSubject = PublishSubject.create<Unit>()
    private val hardReloadEventSubject = PublishSubject.create<Unit>()
    private val refreshEventSubject = PublishSubject.create<Unit>()
    private val reloadPageEventSubject = PublishSubject.create<Unit>()

    @Before
    fun setUp() {
        target = MviPaginationController(paginationController)
        target.setLoadMoreEvent(loadMoreEventSubject)
        target.setHardReloadEvent(hardReloadEventSubject)
        target.setRefreshEvent(refreshEventSubject)
        target.setReloadPageEvent(reloadPageEventSubject)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(paginationController)
    }

    @Test
    fun `init paginationController and restart when call init`() {
        val observable: Observable<List<Any>> = mock()
        val request: (Int) -> Single<out Collection<Any>> = mock()
        target.init(observable, request)
        verify(paginationController).init(observable, request)
        verify(paginationController).restart()
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
    fun `call retry when view reloadPageEventSubject emit event`() {
        reloadPageEventSubject.onNext(Unit)

        verify(paginationController).retry()
    }


}