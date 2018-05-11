package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
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
 * Time: 4:06
 */
@RunWith(MockitoJUnitRunner::class)
class InitialProgressTest {

    private lateinit var target: InitialProgress<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = InitialProgress()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)
        verify(loader).loadFirstPage()
    }

    @Test
    fun restart() {
        assertEquals(InitialProgress<Any>(), target.restart())
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
        assertEquals(Released<Any>(), target.release())
    }

    @Test
    fun `updateCache emptyCache = true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache emptyCache = false`() {
        assertEquals(CachedData<Any>(true), target.updateCache(false))
    }

    @Test
    fun `newPage pageEmpty = true`() {
        assertEquals(EmptyData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage pageEmpty = false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun fail() {
        val throwable = mock<Throwable>()
        assertEquals(EmptyError<Any>(throwable), target.fail(throwable))
    }

}