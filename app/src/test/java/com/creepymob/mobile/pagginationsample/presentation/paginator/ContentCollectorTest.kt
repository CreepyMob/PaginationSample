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
 * Time: 6:27
 */
@RunWith(MockitoJUnitRunner::class)
class ContentCollectorTest {

    private lateinit var target: ContentCollector<Any>
    @Mock private lateinit var collection: MutableCollection<Any>
    @Mock private lateinit var newContent: Collection<Any>

    @Before
    fun setUp() {
        target = ContentCollector(collection)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(collection)
    }

    @Test
    fun getContent() {
        assertEquals(collection, target.content)
    }

    @Test
    fun add() {
        target.add(newContent)
        verify(collection).clear()
        verify(collection).addAll(newContent)
    }

    @Test
    fun clear() {
        target.clear()
        verify(collection).clear()
    }

}