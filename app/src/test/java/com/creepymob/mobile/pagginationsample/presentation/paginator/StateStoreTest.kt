package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 23:44
 */
@RunWith(MockitoJUnitRunner::class)
class StateStoreTest {

    private lateinit var target: StateStore<Any>
    @Mock private lateinit var defaultState: State<Any>

    @Before
    fun setUp() {
        target = StateStore(defaultState)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(defaultState)
    }

    @Test
    fun getState() {
        assertEquals(defaultState, target.state)
    }

    @Test
    fun setState() {
        val newState = mock<State<Any>>()
        target.state = newState
        assertEquals(newState, target.state)
    }

}