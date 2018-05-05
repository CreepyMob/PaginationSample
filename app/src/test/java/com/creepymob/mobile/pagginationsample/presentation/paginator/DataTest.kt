package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
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
 * Time: 5:32
 */
@RunWith(MockitoJUnitRunner::class)
class DataTest {

    private lateinit var target: Data<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var contentThrowable: ContentThrowable
    @Mock private lateinit var content: Collection<Any>

    @Before
    fun setUp() {
        target = Data(contentThrowable)
        whenever(loader.content).thenReturn(content)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }

    @Test
    operator fun invoke() {
        assertFalse(target.invoked())
        assertEquals(ViewState.ContentViewState(content, contentThrowable = contentThrowable), target.invoke(loader))
        assertTrue(target.invoked())

        verify(loader).content
    }

    @Test
    fun restart() {
        assertEquals(EmptyProgress<Any>(), target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(Refresh<Any>(), target.refresh())
    }

    @Test
    fun loadNewPage() {
        assertEquals(PageProgress<Any>(), target.loadNewPage())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}