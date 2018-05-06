package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
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
 * Time: 6:07
 */
@RunWith(MockitoJUnitRunner::class)
class AllDataTest {

    private lateinit var target: AllData<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var content: Collection<Any>

    @Before
    fun setUp() {
        target = AllData()
        whenever(loader.content).thenReturn(content)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    operator fun invoke() {
        assertEquals(ViewState.ContentViewState(content), target.invoke(loader))

        verify(loader).content
    }

    @Test
    fun restart() {
       assertEquals(RestartProgress<Any>(), target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(Refresh<Any>(true), target.refresh())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}