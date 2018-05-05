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

    @Before
    fun setUp() {
        target = StateInvoker()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    fun `invoke invoked state`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val invokedState = mock<State<Any>>()
        whenever(invokedState.invoked()).thenReturn(true)
        target.invoke(invokedState, loader)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke not invoked state when init return null`() {
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()

        val notInvokedState = mock<State<Any>>()
        whenever(notInvokedState.invoke(loader)).thenReturn(null)
        whenever(notInvokedState.invoked()).thenReturn(false)
        target.invoke(notInvokedState, loader)

        testViewState.assertNoValues()
                .assertNotTerminated()
    }

    @Test
    fun `invoke not invoked state when init return ViewState`() {
        val notInvokedState = mock<State<Any>>()
        val viewState = mock<ViewState<Any>>()
        val testViewState = target.viewStateObservable.test()
        testViewState.assertNoValues()
                .assertNotTerminated()


        whenever(notInvokedState.invoke(loader)).thenReturn(viewState)
        whenever(notInvokedState.invoked()).thenReturn(false)
        target.invoke(notInvokedState, loader)

        testViewState.assertValue(viewState)
                .assertNotTerminated()
    }
}