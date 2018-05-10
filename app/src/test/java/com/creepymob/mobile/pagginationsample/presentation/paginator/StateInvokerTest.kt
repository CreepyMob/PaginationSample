package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 23:28
 */
@RunWith(MockitoJUnitRunner::class)
class StateInvokerTest {

    private lateinit var target: StateInvoker<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var viewStateFactory: ViewStateFactory<Any>

    @Before
    fun setUp() {
        target = StateInvoker(viewStateFactory)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver, viewStateFactory)
    }

    @Test
    fun `invoke when previousState equals to newState state`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val previousState = mock<State<Any>>()
        val newState = previousState

        target.invoke(previousState, newState, loader, cacheDataObserver)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke when previousState not equals to newState and factory return null`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val previousState = mock<State<Any>>()
        val newState = mock<State<Any>>()

        whenever(viewStateFactory.create(newState)).thenReturn(null)
        target.invoke(previousState, newState, loader, cacheDataObserver)

        verify(viewStateFactory).create(newState)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke when previousState not equals to newState and factory return ViewState`() {
        val previousState = mock<State<Any>>()
        val newState = mock<State<Any>>()
        val viewState = mock<ViewState<Any>>()
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()


        whenever(viewStateFactory.create(newState)).thenReturn(viewState)

        target.invoke(previousState, newState, loader, cacheDataObserver)

        verify(viewStateFactory).create(newState)
        testViewState.assertValue(viewState)
                .assertNotTerminated()
    }
}