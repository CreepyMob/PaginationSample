package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 23:02
 *
 */
interface ContentStore<T> {

    val content: Collection<T>
}