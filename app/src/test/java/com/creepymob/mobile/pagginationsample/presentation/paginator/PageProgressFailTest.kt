package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 12.05.2018
 * Time: 1:52
 */
@RunWith(MockitoJUnitRunner::class)
class PageProgressFailTest {

    private lateinit var target: PageProgressFail<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var throwable: Throwable
    @Before
    fun setUp() {
        target = PageProgressFail(throwable)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)
        verifyZeroInteractions(loader)
        verifyZeroInteractions(cacheDataObserver)
    }

    @Test
    fun restart() {
        assertEquals(RestartProgress<Any>(), target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(Refresh(target), target.refresh())
    }

    @Test
    fun retry() {
        assertEquals(PageProgress<Any>(), target.retry())
    }

    @Test
    fun loadNewPage() {
        assertSame(target, target.loadNewPage())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

    @Test
    fun `updateCache when emptyCache = true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache when emptyCache = false`() {
        assertSame(target, target.updateCache(false))
    }

    @Test
    fun `newPage when pageEmpty = true`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun `newPage when pageEmpty = false`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun fail() {
        val error = mock<Throwable>()
        assertSame(target, target.fail(error))
        verifyZeroInteractions(error)
    }

}