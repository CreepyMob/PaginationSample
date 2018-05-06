package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.inOrder
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
 * Time: 6:01
 */
@RunWith(MockitoJUnitRunner::class)
class PageProgressTest {

    private lateinit var target: PageProgress<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var throwable: Throwable
    @Mock private lateinit var content: Collection<Any>
    @Mock private lateinit var newContent: Collection<Any>

    @Before
    fun setUp() {
        target = PageProgress()
        whenever(loader.content).thenReturn(content)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader)
    }


    @Test
    operator fun invoke() {

        assertEquals(ViewState.ContentViewState(content, isNextPageLoaded = true), target.invoke(loader))


        inOrder(loader).apply {
            verify(loader).loadNextPage()
            verify(loader).content
        }
    }

    @Test
    fun restart() {
        assertEquals(RestartProgress<Any>(), target.restart())
    }

    @Test
    fun `newPage with pageEmpty = true`() {
        assertEquals(AllData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage with pageEmpty = false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun refresh() {
        assertEquals(Refresh<Any>(), target.refresh())
    }

    @Test
    fun fail() {
        assertEquals(PageProgressFail<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}