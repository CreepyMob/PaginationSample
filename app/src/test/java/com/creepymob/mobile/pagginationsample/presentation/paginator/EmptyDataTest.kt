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
 * Time: 5:24
 */
@RunWith(MockitoJUnitRunner::class)
class EmptyDataTest {

    private lateinit var target: EmptyData<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>

    @Before
    fun setUp() {
        target = EmptyData()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    operator fun invoke() {

        assertEquals(ViewState.EmptyContentViewState<Any>(), target.invoke(loader))

    }

    @Test
    fun restart() {
        assertEquals(RestartProgress<Any>(), target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(EmptyDataRefresh<Any>(), target.refresh())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}