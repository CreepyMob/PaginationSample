package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import com.creepymob.mobile.pagginationsample.app.SchedulersProviderTest
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

/**
 * User: andrey
 * Date: 06.05.2018
 * Time: 0:19
 */
@RunWith(MockitoJUnitRunner::class)
class WaitUntilCollectorReceiveNewContentTest {

    private lateinit var target: WaitUntilCollectorReceiveNewContent<Any>
    @Mock private lateinit var collector: ContentCollector<Any>
    @Spy private val schedulersProvider: SchedulersProvider = SchedulersProviderTest()
    private val contentTestSubject: BehaviorSubject<Collection<Any>> = BehaviorSubject.createDefault(listOf())

    @Before
    fun setUp() {
        target = WaitUntilCollectorReceiveNewContent(schedulersProvider)

        whenever(collector.contentObservable).thenReturn(contentTestSubject)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(collector, schedulersProvider)
    }

    @Test
    fun `invoke when empty content receive`() {

        val content = emptyList<Any>()
        val result = target.invoke(content, collector).test()

        verify(collector, never()).contentObservable

        result.assertValue { it === content }
                .assertTerminated()
                .assertNoErrors()
    }

    @Test
    fun `invoke when not empty content receive and collector update ith newContent before invoke`() {

        val newContent = listOf<Any>(mock())
        contentTestSubject.onNext(newContent)

        val result = target.invoke(newContent, collector).test()

        verify(schedulersProvider).io()
        verify(collector).contentObservable

        result.assertValue { it === newContent }
                .assertTerminated()
                .assertNoErrors()
    }

    @Test
    fun `invoke when not empty content receive and collector update with newContent after invoke`() {

        val newContent = listOf<Any>(1)
        val result = target.invoke(newContent, collector).test()
        val currentCollectorContent = mutableListOf<Any>(mock(), mock(), mock(), mock(), mock(), mock())
        contentTestSubject.onNext(currentCollectorContent)

        verify(schedulersProvider).io()
        verify(collector).contentObservable

        result.assertNoValues()
                .assertNotTerminated()

        contentTestSubject.onNext(currentCollectorContent.apply {
            addAll(newContent)
        })

        result.assertValue { it === newContent }
                .assertTerminated()
                .assertNoErrors()
    }

    @Test
    fun `invoke when not empty content receive and collector update with not newContent after invoke`() {

        val newContent = listOf<Any>(1)
        val result = target.invoke(newContent, collector).test()
        val currentCollectorContent = mutableListOf<Any>(mock(), mock(), mock(), mock(), mock(), mock())
        contentTestSubject.onNext(currentCollectorContent)

        verify(schedulersProvider).io()
        verify(collector).contentObservable

        result.assertNoValues()
                .assertNotTerminated()

        contentTestSubject.onNext(currentCollectorContent.apply {
            add(mock())
            add(mock())
            add(mock())
        })

        result.assertNoValues()
                .assertNotTerminated()

        contentTestSubject.onNext(currentCollectorContent.apply {
            removeAt(0)
        })

        result.assertNoValues()
                .assertNotTerminated()
    }
}