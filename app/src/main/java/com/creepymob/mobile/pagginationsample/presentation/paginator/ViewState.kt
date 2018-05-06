package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 0:34
 *
 */
sealed class ViewState<T> {

    override fun equals(other: Any?): Boolean = if (other == null) false else other::class == this::class

    override fun hashCode(): Int = javaClass.hashCode()

    class EmptyLoadingViewState<T> : ViewState<T>()

    class EmptyContentViewState<T>(val isPassiveProgress: Boolean = false) : ViewState<T>()

    data class EmptyListErrorViewState<T>(val throwable: Throwable, val isPassiveProgress: Boolean = false) : ViewState<T>()

    data class ContentViewState<T>(val content: Collection<T>,
                                   val isPassiveProgress: Boolean = false,
                                   val isNextPageLoaded: Boolean = false,
                                   val isRefresh: Boolean = false,
                                   val contentThrowable: ContentThrowable? = null) : ViewState<T>()
}

