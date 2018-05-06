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
 * Time: 3:11
 */
@RunWith(MockitoJUnitRunner::class)
class EmptyStateTest {

    private lateinit var target: EmptyState<Any>

    @Mock private lateinit var loader: PageContentLoader<Any>

    @Before
    fun setUp() {
        target = EmptyState()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    fun invoke() {
        assertFalse(target.invoked())
        assertEquals(null, target.invoke(loader))
        assertTrue(target.invoked())
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