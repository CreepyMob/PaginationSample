package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
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
 * Time: 5:32
 */
@RunWith(MockitoJUnitRunner::class)
class DataTest {

    private lateinit var target: Data<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>

    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var throwable: Throwable //TODO

    @Before
    fun setUp() {
        target = Data()
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
    fun loadNewPage() {
        assertEquals(PageProgress<Any>(), target.loadNewPage())
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}