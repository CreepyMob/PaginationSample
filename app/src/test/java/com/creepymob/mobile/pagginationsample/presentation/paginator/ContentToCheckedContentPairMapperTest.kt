package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
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
 * Time: 15:56
 */
@RunWith(MockitoJUnitRunner::class)
class ContentToCheckedContentPairMapperTest {

    private lateinit var target: ContentToCheckedContentPairMapper<Any>
    @Mock private lateinit var collectionChecker: (oldCollection: Collection<*>, newCollection: Collection<*>) -> Boolean
    @Mock private lateinit var collector: ContentCollector<Any>
    @Mock private lateinit var appliedCollection: Collection<Any>
    @Mock private lateinit var content: Collection<Any>

    @Before
    fun setUp() {
        target = ContentToCheckedContentPairMapper(collector, collectionChecker)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(collector, collectionChecker)
    }

    @Test
    fun apply() {
        whenever(collector.content).thenReturn(content)
        whenever(collectionChecker(content, appliedCollection)).thenReturn(true)

        assertEquals(Pair(appliedCollection, true), target.apply(appliedCollection))
        verify(collectionChecker).invoke(content, appliedCollection)
        verify(collector).content
    }

}