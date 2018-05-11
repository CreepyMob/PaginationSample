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
 * Date: 30.04.2018
 * Time: 5:18
 */
@RunWith(MockitoJUnitRunner::class)
class EmptyErrorTest {

    private lateinit var target: EmptyError<Any>
    @Mock private lateinit var throwable: Throwable
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = EmptyError(throwable)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)
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
        assertEquals(target, target.retry())
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
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `newPage pageEmpty = true`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun `newPage pageEmpty = false`() {
        assertSame(target, target.newPage(false))
    }

    @Test
    fun fail() {
        val error = mock<Throwable>()
        assertSame(target, target.fail(error))
        verifyZeroInteractions(error)
    }

}