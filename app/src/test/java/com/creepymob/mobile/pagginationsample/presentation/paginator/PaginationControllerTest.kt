package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 20:39
 */
@RunWith(MockitoJUnitRunner::class)
class PaginationControllerTest {

    private lateinit var target:  PaginationController<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>

    @Mock private lateinit var invoker: StateInvoker<Any>
    @Mock private lateinit var stateMachine: PaginationStateMachine<Any>
    @Mock private lateinit var viewStateObservable: Observable<ViewState<Any>>
    @Mock private lateinit var request: (Int) -> Single<out Collection<Any>>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var observable: Observable<List<Any>>

    @Before
    fun setUp() {
        target = PaginationController(invoker, stateMachine, loader, cacheDataObserver)

        whenever(invoker.viewStateObservable).thenReturn(viewStateObservable)
    }

    @After
    fun tearDown() {

        verifyNoMoreInteractions(loader, invoker, stateMachine, cacheDataObserver)
    }

    @Test
    fun getViewStateObservable() {

        assertEquals(viewStateObservable, target.viewStateObservable)

        verify(invoker).viewStateObservable
    }

    @Test
    fun init() {

        target.init(request, observable)

        verify(loader).init(request, stateMachine)
        verify(cacheDataObserver).init(observable, stateMachine)
    }

    @Test
    fun restart() {
        target.restart()

        verify(stateMachine).restart()
    }

    @Test
    fun refresh() {
        target.refresh()

        verify(stateMachine).refresh()
    }

    @Test
    fun retry() {
        target.retry()

        verify(stateMachine).retry()
    }

    @Test
    fun loadNewPage() {
        target.loadNewPage()

        verify(stateMachine).loadNewPage()
    }

    @Test
    fun release() {
        target.release()

        verify(stateMachine).release()
    }

}