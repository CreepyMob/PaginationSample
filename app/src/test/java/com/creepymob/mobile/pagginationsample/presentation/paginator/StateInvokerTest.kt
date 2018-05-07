package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
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
    @Mock private lateinit var collStore: ContentStore<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = StateInvoker()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, collStore, cacheDataObserver)
    }

    @Test
    fun `invoke when previousState equals to newState state`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val previousState = mock<State<Any>>()
        val newState = previousState

        target.invoke(previousState, newState, loader, collStore, cacheDataObserver)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke when previousState not equals to newState and init return null`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val previousState = mock<State<Any>>()
        val newState = mock<State<Any>>()

        whenever(newState.invoke(loader, collStore, cacheDataObserver)).thenReturn(null)
        target.invoke(previousState, newState, loader, collStore, cacheDataObserver)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke when previousState not equals to newState and init return ViewState`() {
        val previousState = mock<State<Any>>()
        val newState = mock<State<Any>>()
        val viewState = mock<ViewState<Any>>()
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()


        whenever(newState.invoke(loader, collStore, cacheDataObserver)).thenReturn(viewState)

        target.invoke(previousState, newState, loader, collStore, cacheDataObserver)

        testViewState.assertValue(viewState)
                .assertNotTerminated()
    }
}