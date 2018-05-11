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
 * Time: 3:11
 */
@RunWith(MockitoJUnitRunner::class)
class InitialStateTest {

    private lateinit var target: InitialState<Any>

    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = InitialState()
    }

    @After
    fun tearDown() {
           verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    fun invoke() {
        target.invoke(loader, cacheDataObserver )
    }

    @Test
    fun restart() {
        assertSame(target, target.restart())
    }

    @Test
    fun refresh() {
        assertEquals(InitialProgress<Any>(), target.refresh())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
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
    fun `updateCache when emptyCache = true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache when emptyCache = false`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `newPage when pageEmpty = true`() {
        assertSame(target, target.newPage(true))
    }

    @Test
    fun `newPage when pageEmpty = false`() {
        assertSame(target, target.newPage(false))
    }

    @Test
    fun fail() {
        val error = mock<Throwable>()
        assertSame(target, target.fail(error))
        verifyZeroInteractions(error)
    }

}