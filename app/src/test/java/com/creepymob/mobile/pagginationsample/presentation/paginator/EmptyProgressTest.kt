package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
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
 * Time: 4:06
 */
@RunWith(MockitoJUnitRunner::class)
class EmptyProgressTest {

    private lateinit var target: EmptyProgress<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>

    @Before
    fun setUp() {
        target = EmptyProgress()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    operator fun invoke() {
        assertFalse(target.invoked())
        assertEquals(ViewState.EmptyLoadingViewState<Any>(), target.invoke(loader))
        verify(loader).loadFirstPage(target)
        assertTrue(target.invoked())
    }

    @Test
    fun restart() {
        assertEquals(EmptyProgress<Any>(), target.restart())
    }

    @Test
    fun `newPage with pageEmpty true`() {
        assertEquals(EmptyData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage with pageEmpty false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun fail() {
        val throwable = mock<Throwable>()
        assertEquals(EmptyError<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun release() {

        assertEquals(Released<Any>(), target.release())
    }

}