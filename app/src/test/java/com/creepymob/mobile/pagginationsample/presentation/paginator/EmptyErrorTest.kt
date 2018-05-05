package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 5:18
 */
@RunWith(MockitoJUnitRunner::class)
class EmptyErrorTest {

    private lateinit var target : EmptyError<Any>
    @Mock private lateinit var throwable: Throwable
    @Mock private lateinit var loader: PageContentLoader<Any>

    @Before
    fun setUp() {
        target = EmptyError(throwable)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    operator fun invoke() {
        assertFalse(target.invoked())
        assertEquals(ViewState.EmptyListErrorViewState<Any>(throwable), target.invoke(loader))
        assertTrue(target.invoked())
    }

    @Test
    fun restart() {
        assertEquals(EmptyProgress<Any>(), target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(EmptyProgress<Any>(), target.refresh())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}