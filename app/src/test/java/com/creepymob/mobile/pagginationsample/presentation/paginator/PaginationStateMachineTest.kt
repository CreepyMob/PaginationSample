package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
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
    @Mock private lateinit var loader: PageContentLoader<Any>
    @Mock private lateinit var stateStore: StateStore<Any>
    @Mock private lateinit var invoker: StateInvoker<Any>
    @Mock private lateinit var request: (Int) -> Single<out Collection<Any>>
    @Mock private lateinit var initialState: State<Any>

    @Before
    fun setUp() {
        target = PaginationStateMachine(invoker, loader, stateStore)

        whenever(stateStore.state).thenReturn(initialState)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(loader, stateStore, invoker, initialState)
    }


    @Test
    fun init() {
        target.init(request)

        verify(loader).init(request, target)
        verify(stateStore).state
        verify(stateStore).state = initialState
        verify(invoker).invoke(initialState, loader)
    }


    @Test
    fun restart() {
        val restartState = mock<State<Any>>()
        whenever(initialState.restart()).thenReturn(restartState)

        target.restart()
        inOrder(loader, stateStore, invoker, initialState).apply {
            verify(stateStore).state
            verify(initialState).restart()
            verify(stateStore).state = restartState
            verify(invoker).invoke(restartState, loader)
        }
    }

    @Test
    fun refresh() {
        val refreshState = mock<State<Any>>()
        whenever(initialState.refresh()).thenReturn(refreshState)

        target.refresh()
        inOrder(loader, stateStore, invoker, initialState).apply {
            verify(stateStore).state
            verify(initialState).refresh()
            verify(stateStore).state = refreshState
            verify(invoker).invoke(refreshState, loader)
        }
    }

    @Test
    fun loadNewPage() {
        val loadNewPageState = mock<State<Any>>()
        whenever(initialState.loadNewPage()).thenReturn(loadNewPageState)

        target.loadNewPage()
        inOrder(loader, stateStore, invoker, initialState).apply {
            verify(stateStore).state
            verify(initialState).loadNewPage()
            verify(stateStore).state = loadNewPageState
            verify(invoker).invoke(loadNewPageState, loader)
        }
    }

    @Test
    fun release() {
        val releaseState = mock<State<Any>>()
        whenever(initialState.release()).thenReturn(releaseState)

        target.release()
        inOrder(loader, stateStore, invoker, initialState).apply {
            verify(stateStore).state
            verify(initialState).release()
            verify(stateStore).state = releaseState
            verify(invoker).invoke(releaseState, loader)
        }
    }

}