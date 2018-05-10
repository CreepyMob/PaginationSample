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
 * Time: 6:01
 */
@RunWith(MockitoJUnitRunner::class)
class PageProgressTest {

    private lateinit var target: PageProgress<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>

    @Mock private lateinit var throwable: Throwable

    @Before
    fun setUp() {
        target = PageProgress()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, cacheDataObserver)
    }

    @Test
    operator fun invoke() {
        target.invoke(loader, cacheDataObserver)

        verify(loader).loadNextPage()
    }

    @Test
    fun restart() {
        assertEquals(RestartProgress<Any>(), target.restart())
    }

    @Test
    fun `newPage with pageEmpty = true`() {
        assertEquals(AllData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage with pageEmpty = false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun refresh() {
        assertEquals(Refresh<Any>(target), target.refresh())
    }

    @Test
    fun fail() {
        assertEquals(PageProgressFail<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun release() {
        assertEquals(Released<Any>(), target.release())
    }

}