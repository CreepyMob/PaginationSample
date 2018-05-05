package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 30.04.2018
 * Time: 1:09
 *
 */
data class ContentThrowable(val throwable: Throwable, val whenNextPageLoaded: Boolean = false, val whenRefresh: Boolean = false)