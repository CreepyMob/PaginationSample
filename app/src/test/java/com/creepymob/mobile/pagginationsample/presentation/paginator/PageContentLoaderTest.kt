package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
 * Time: 6:44
 */
@RunWith(MockitoJUnitRunner::class)
class PageContentLoaderTest {

    private lateinit var target: PageContentLoader<Any>
    @Mock private lateinit var collector: ContentCollector<Any>
    @Mock private lateinit var counter: PageCounter
    private val subscribeScheduler: Scheduler = Schedulers.trampoline()
    private val observeScheduler: Scheduler = Schedulers.trampoline()
    @Mock private lateinit var disposable: CompositeDisposable
    @Mock private lateinit var contentPairMapper: ContentToCheckedContentPairMapper<Any>
    @Mock private lateinit var request: (Int) -> Single<out Collection<Any>>
    @Mock private lateinit var stateMachine: PaginationStateMachine<Any>

    @Mock private lateinit var content: Collection<Any>

    @Mock private lateinit var currentState: State<Any>


    @Before
    fun setUp() {
        target = PageContentLoader(subscribeScheduler, observeScheduler, collector, contentPairMapper, disposable, counter)
        target.init(request, stateMachine)
        whenever(collector.content).thenReturn(content)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(collector, contentPairMapper, disposable, counter, stateMachine)

    }

    @Test
    fun getContent() {
        assertSame(content, target.content)
        verify(collector).content
    }


    @Test
    fun `loadFirstPage when request failed`() {
        val throwable = RuntimeException()
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(0)).thenReturn(Single.error(throwable))

        target.loadFirstPage(currentState)

        verify(collector).clear()

        inOrder(counter).apply {
            verify(counter).reset()
            verify(counter).currentPage
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }


        verify(currentState).fail(throwable)
    }

    @Test
    fun `loadFirstPage when request success`() {
        val newContent = mock<Collection<Any>>()
        val contentPairMap = Pair(newContent, true)
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(0)).thenReturn(Single.just(newContent))
        whenever(contentPairMapper.apply(newContent)).thenReturn(contentPairMap)

        target.loadFirstPage(currentState)

        verify(contentPairMapper).apply(newContent)

        inOrder(collector).apply {
            verify(collector).clear()
            verify(collector).add(contentPairMap.first)
        }

        inOrder(counter).apply {
            verify(counter).reset()
            verify(counter).currentPage
            verify(counter).increment()
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }

        verify(currentState).newPage(contentPairMap.second)
    }

    @Test
    fun `loadNextPage when request failed`() {
        val throwable = RuntimeException()
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(1)).thenReturn(Single.error(throwable))

        target.loadNextPage(currentState)

        inOrder(counter).apply {
            verify(counter).currentPage
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(1)
            verify(disposable).add(any())
        }


        verify(currentState).fail(throwable)
    }

    @Test
    fun `loadNextPage when request success`() {
        val newContent = mock<Collection<Any>>()
        val contentPairMap = Pair(newContent, true)
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(1)).thenReturn(Single.just(newContent))
        whenever(contentPairMapper.apply(newContent)).thenReturn(contentPairMap)

        target.loadNextPage(currentState)

        verify(contentPairMapper).apply(newContent)

        inOrder(collector).apply {
            verify(collector).add(contentPairMap.first)
        }

        inOrder(counter).apply {
            verify(counter).currentPage
            verify(counter).increment()
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(1)
            verify(disposable).add(any())
        }

        verify(currentState).newPage(contentPairMap.second)
    }

    @Test
    fun loadNextPage() {

    }

    @Test
    fun release() {
        target.release()
        verify(disposable).dispose()
    }

}