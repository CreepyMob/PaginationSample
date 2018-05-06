package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 16:52
 */
@RunWith(MockitoJUnitRunner::class)
class PaginationStateMachineTest {

    private lateinit var target: PaginationStateMachine<Any>
    //@Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var stateStore: StateStore<Any>
    @Mock private lateinit var stateApplier: StateApplier<Any>
    @Mock private lateinit var initialState: State<Any>

    @Before
    fun setUp() {
        target = PaginationStateMachine(stateStore, stateApplier)
        whenever(stateStore.state).thenReturn(initialState)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(stateApplier, stateStore)
    }

    @Test
    fun restart() {
        val restartState = mock<State<Any>>()
        whenever(initialState.restart()).thenReturn(restartState)

        target.restart()
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).restart()
            verify(stateApplier).apply(restartState)
        }
    }

    @Test
    fun refresh() {
        val refreshState = mock<State<Any>>()
        whenever(initialState.refresh()).thenReturn(refreshState)

        target.refresh()
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).refresh()
            verify(stateApplier).apply(refreshState)
        }
    }

    @Test
    fun loadNewPage() {
        val loadNewPageState = mock<State<Any>>()
        whenever(initialState.loadNewPage()).thenReturn(loadNewPageState)

        target.loadNewPage()
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).loadNewPage()
            verify(stateApplier).apply(loadNewPageState)
        }
    }

    @Test
    fun release() {
        val releaseState = mock<State<Any>>()
        whenever(initialState.release()).thenReturn(releaseState)

        target.release()
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).release()
            verify(stateApplier).apply(releaseState)
        }
    }

    @Test
    fun retry() {
        val retryState = mock<State<Any>>()
        whenever(initialState.retry()).thenReturn(retryState)

        target.retry()
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).retry()
            verify(stateApplier).apply(retryState)
        }
    }

    @Test
    fun newPage() {
        val newPageState = mock<State<Any>>()
        val emptyPage = true
        whenever(initialState.newPage(same(emptyPage))).thenReturn(newPageState)

        target.newPage(emptyPage)
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).newPage(same(emptyPage))
            verify(stateApplier).apply(newPageState)
        }
    }

    @Test
    fun updateCache() {
        val updateCacheState = mock<State<Any>>()
        val emptyCache = true
        whenever(initialState.updateCache(emptyCache)).thenReturn(updateCacheState)

        target.updateCache(emptyCache)
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).updateCache(same(emptyCache))
            verify(stateApplier).apply(updateCacheState)
        }
    }

    @Test
    fun fail() {
        val updateCacheState = mock<State<Any>>()
        val throwable = mock<Throwable>()
        whenever(initialState.fail(throwable)).thenReturn(updateCacheState)

        target.fail(throwable)
        inOrder(stateApplier, stateStore, initialState).apply {
            verify(stateStore).state
            verify(initialState).fail(throwable)
            verify(stateApplier).apply(updateCacheState)
        }
    }
}