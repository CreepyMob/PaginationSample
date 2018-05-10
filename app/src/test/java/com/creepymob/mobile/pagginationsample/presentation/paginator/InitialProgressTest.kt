package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.mock
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
    fun `newPage with pageEmpty true`() {
        assertEquals(EmptyData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage with pageEmpty false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun `updateCache with emptyCache true`() {
        assertEquals(target, target.updateCache(true))
    }

    @Test
    fun `updateCache with emptyCache false`() {
        assertEquals(CachedData<Any>(true), target.updateCache(false))
    }

    @Test
    fun fail() {
        val throwable = mock<Throwable>()
        assertEquals(EmptyError<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun release() {

        assertEquals(Released<Any>(), target.release())
    }

}