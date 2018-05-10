package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.creepymob.mobile.pagginationsample.app.SchedulersProviderTest
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Spy
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
    @Spy private val schedulersProvider = SchedulersProviderTest()

    @Mock private lateinit var disposable: CompositeDisposable
    @Mock private lateinit var waitUntilCollectorReceiveNewContent: WaitUntilCollectorReceiveNewContent<Any>
    @Mock private lateinit var request: (Int) -> Single<Collection<Any>>
    @Mock private lateinit var stateMachine: PaginationStateMachine<Any>

    @Before
    fun setUp() {
        target = PageContentLoader(collector, schedulersProvider, waitUntilCollectorReceiveNewContent, disposable, counter)
        target.init(request, stateMachine)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(collector, schedulersProvider, waitUntilCollectorReceiveNewContent, disposable, counter)

    }

    @Test
    fun `loadFirstPage when request failed`() {
        val throwable = RuntimeException()
        whenever(request(0)).thenReturn(Single.error(throwable))

        target.loadFirstPage()

        inOrder(schedulersProvider).apply {
            verify(schedulersProvider).io()
            verify(schedulersProvider).main()
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }

        verify(stateMachine).fail(throwable)
    }

    @Test
    fun `loadFirstPage when request success`() {
        val isNewContentEmpty = true
        val newContent = mock<Collection<Any>>()
        whenever(newContent.isEmpty()).thenReturn(isNewContentEmpty)
        whenever(request(0)).thenReturn(Single.just(newContent))
        whenever(waitUntilCollectorReceiveNewContent.invoke(newContent, collector)).thenReturn(Single.just(newContent))

        target.loadFirstPage()

        verify(waitUntilCollectorReceiveNewContent).invoke(newContent, collector)

        inOrder(schedulersProvider).apply {
            verify(schedulersProvider).io()
            verify(schedulersProvider).main()
        }

        verify(counter).incrementAndSet(0)

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }

        verify(stateMachine).newPage(same(isNewContentEmpty))
    }

    @Test
    fun `loadNextPage when request failed`() {
        val throwable = RuntimeException()
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(0)).thenReturn(Single.error(throwable))

        target.loadNextPage()

        verify(counter).currentPage

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }

        inOrder(schedulersProvider).apply {
            verify(schedulersProvider).io()
            verify(schedulersProvider).main()
        }

        verify(stateMachine).fail(throwable)
    }

    @Test
    fun `loadNextPage when request success`() {
        val isNewContentEmpty = true
        val newContent = mock<Collection<Any>>()
        whenever(newContent.isEmpty()).thenReturn(isNewContentEmpty)
        whenever(counter.currentPage).thenReturn(0)
        whenever(request(0)).thenReturn(Single.just(newContent))
        whenever(waitUntilCollectorReceiveNewContent.invoke(newContent, collector)).thenReturn(Single.just(newContent))

        target.loadNextPage()

        verify(waitUntilCollectorReceiveNewContent).invoke(newContent, collector)


        inOrder(schedulersProvider).apply {
            verify(schedulersProvider).io()
            verify(schedulersProvider).main()
        }

        inOrder(counter).apply {
            verify(counter).currentPage
            verify(counter).incrementAndSet(0)
        }

        inOrder(disposable, request).apply {
            verify(disposable).clear()
            verify(request).invoke(0)
            verify(disposable).add(any())
        }

        verify(stateMachine).newPage(same(isNewContentEmpty))
    }


    @Test
    fun release() {
        target.release()
        verify(disposable).dispose()
    }

}