package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 20:35
 */
@RunWith(MockitoJUnitRunner::class)
class StateApplierTest {

    private lateinit var target: StateApplier<Any>

    @Mock private lateinit var stateStore: StateStore<Any>
    @Mock private lateinit var invoker: StateInvoker<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>


    @Before
    fun setUp() {
        target = StateApplier(loader, stateStore, invoker)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, stateStore, invoker)
    }

    @Test
    fun apply() {
        val newState = mock<State<Any>>()
        target.apply(newState)

        verify(stateStore).state = newState
        verify(invoker).invoke(newState, loader)
    }

}