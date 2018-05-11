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
 * Time: 5:46
 */
@RunWith(MockitoJUnitRunner::class)
class RefreshTest {

    private lateinit var target: Refresh<Any>
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var cacheDataObserver: CacheDataObserver<Any>
    @Mock private lateinit var throwable: Throwable
    @Mock private lateinit var previousState: State<Any>

    @Before
    fun setUp() {
        target = Refresh(previousState)
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
    fun `updateCache when emptyCache = true`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `updateCache when emptyCache = false`() {
        assertSame(target, target.updateCache(true))
    }

    @Test
    fun `newPage with pageEmpty = true`() {
        assertEquals(EmptyData<Any>(), target.newPage(true))
    }

    @Test
    fun `newPage with pageEmpty = false`() {
        assertEquals(Data<Any>(), target.newPage(false))
    }

    @Test
    fun `fail when previousState unknown State`() {
        assertEquals(Data<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun `fail when previousState CachedData`() {
        val target = Refresh(CachedData<Any>(true, mock()))
        assertEquals(CachedData<Any>(false, CachedThrowable(throwable, Refresh::class)), target.fail(throwable))
    }

    @Test
    fun `fail when previousState AllData`() {
        val target = Refresh(mock<AllData<Any>>())
        assertEquals(AllData<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun `fail when previousState EmptyData`() {
        val target = Refresh(mock<EmptyData<Any>>())
        assertEquals(EmptyError<Any>(throwable), target.fail(throwable))
    }

    @Test
    fun `fail when previousState Data`() {
        val target = Refresh(mock<Data<Any>>())
        assertEquals(Data<Any>(throwable), target.fail(throwable))
    }
}