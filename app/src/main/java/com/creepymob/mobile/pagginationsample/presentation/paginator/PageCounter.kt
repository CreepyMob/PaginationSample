package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 6:40
 *
 */
class PageCounter(defaultPage: Int = 0) {

    var currentPage = defaultPage
        private set

    fun incrementAndSet(page: Int) {
        currentPage = page + 1
    }
}