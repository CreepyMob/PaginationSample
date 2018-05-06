package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.nhaarman.mockito_kotlin.whenever
import junit.framework.TestCase.assertEquals
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

    private lateinit var target: ContentToIsEmptyContentMapper<Any>
    @Mock private lateinit var appliedCollection: Collection<Any>


    @Before
    fun setUp() {
        target = ContentToIsEmptyContentMapper()
    }


    @Test
    fun `apply when appliedCollection empty`() {

        whenever(appliedCollection.isEmpty()).thenReturn(true)

        assertEquals(Pair(appliedCollection, true), target.apply(appliedCollection))

    }

    @Test
    fun `apply when appliedCollection not empty`() {

        whenever(appliedCollection.isEmpty()).thenReturn(false)

        assertEquals(Pair(appliedCollection, false), target.apply(appliedCollection))

    }

}