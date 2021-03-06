package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import junit.framework.TestCase.assertSame
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 6:22
 */
@RunWith(MockitoJUnitRunner::class)
class ReleasedTest {

    private lateinit var target: Released<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = Released()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)
        verify(loader).release()
        verify(cacheDataObserver).release()
    }

    @Test
    fun restart() {
        assertSame(target, target.restart())
    }

    @Test
    fun refresh() {
        assertSame(target, target.refresh())
    }

    @Test
    fun retry() {
        assertSame(target, target.retry())
    }

    @Test
    fun loadNewPage() {
        assertSame(target, target.loadNewPage())
    }

    @Test
    fun release() {
        assertSame(target, target.release())
    }

    @Test
    fun `updateCache emptyCache = true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache emptyCache = false`() {
        assertSame(target, target.updateCache(false))
    }

    @Test
    fun `newPage pageEmpty = true`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun `newPage pageEmpty = false`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun fail() {
        val error = mock<Throwable>()
        assertSame(target, target.fail(error))
        verifyZeroInteractions(error)
    }

}