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
class CachedDataTest {
    private lateinit var target: CachedData<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = CachedData(true)
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
    fun `updateCache with emptyCache true`() {
        assertEquals(EmptyData<Any>(), target.updateCache(true))
    }

    @Test
    fun `CachedData passiveProgress = true updateCache with emptyCache false`() {
        assertEquals(target, target.updateCache(false))
    }

    @Test
    fun `CachedData passiveProgress = false updateCache with emptyCache false`() {
        assertEquals(target, target.updateCache(false))
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
        assertEquals(CachedData<Any>(false, CachedThrowable(throwable, CachedData::class)), target.fail(throwable))
    }

}