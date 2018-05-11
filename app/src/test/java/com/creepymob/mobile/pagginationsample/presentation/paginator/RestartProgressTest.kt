package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
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
 * Time: 1:35
 */
@RunWith(MockitoJUnitRunner::class)
class RestartProgressTest {

    private lateinit var target: RestartProgress<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = RestartProgress()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)
        verify(loader).loadFirstPage()
        verifyZeroInteractions(cacheDataObserver)
    }

    @Test
    fun restart() {
        assertEquals(RestartProgress<Any>(), target.restart())
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
    fun `updateCache with emptyCache true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache with emptyCache false`() {
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
        assertEquals(EmptyError<Any>(throwable), target.fail(throwable))
    }

}