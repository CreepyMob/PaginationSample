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
 * Time: 5:32
 */
@RunWith(MockitoJUnitRunner::class)
class DataTest {

    private lateinit var target: Data<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var throwable: Throwable

    @Before
    fun setUp() {
        target = Data(throwable)
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
        assertEquals(PageProgress<Any>(), target.loadNewPage())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

    @Test
    fun `updateCache when emptyCache = true`() {
        assertEquals(EmptyData<Any>(), target.updateCache(true))
    }

    @Test
    fun `Data without throwable updateCache when emptyCache = false`() {
        val nullThrowableData = Data<Any>()
        nullThrowableData.updateCache(false).also {
            assertEquals(Data<Any>(), it)
            assertNotSame(nullThrowableData, it)
        }
    }

    @Test
    fun `Data with throwable updateCache when emptyCache = false`() {
        target.updateCache(false).also {
            assertEquals(Data<Any>(throwable), it)
            assertNotSame(target, it)
        }
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