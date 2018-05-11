package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
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
 * Time: 6:07
 */
@RunWith(MockitoJUnitRunner::class)
class AllDataTest {

    private lateinit var target: AllData<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = AllData()
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
        assertSame(target, target.retry())
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
    fun `updateCache emptyCache = true`() {
        assertEquals(EmptyData<Any>(), target.updateCache(true))
    }

    @Test
    fun `updateCache emptyCache = false`() {
        target.updateCache(false).also {
            assertEquals(AllData<Any>(), it)
            assertNotSame(target, it)
        }
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