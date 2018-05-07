package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verify
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
 * Time: 6:22
 */
@RunWith(MockitoJUnitRunner::class)
class ReleasedTest {

    private lateinit var target: Released<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var collector: ContentCollector<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Before
    fun setUp() {
        target = Released()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, collector, cacheDataObserver)
    }

    @Test
    operator fun invoke() {

        assertEquals(null, target.invoke(loader, collector, cacheDataObserver))

        verify(loader).release()
        verify(cacheDataObserver).release()
    }

}