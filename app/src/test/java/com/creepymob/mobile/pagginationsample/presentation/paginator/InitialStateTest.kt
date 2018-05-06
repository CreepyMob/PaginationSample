package com.creepymob.mobile.pagginationsample.presentation.paginator

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
 * Time: 3:11
 */
@RunWith(MockitoJUnitRunner::class)
class InitialStateTest {

    private lateinit var target: InitialState<Any>

    @Mock private lateinit var loader: PageContentLoader<Any>

    @Before
    fun setUp() {
        target = InitialState()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    fun invoke() {

        assertEquals(null, target.invoke(loader))

    }

    @Test
    fun refresh() {
        assertEquals(InitialProgress<Any>(), target.refresh())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}