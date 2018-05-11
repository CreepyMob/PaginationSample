package com.creepymob.mobile.pagginationsample.presentation.paginator

import kotlin.reflect.KClass

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:47
 *
 */

interface State<T> {

    fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {}
    fun restart(): State<T> = this
    fun refresh(): State<T> = this
    fun retry(): State<T> = this
    fun loadNewPage(): State<T> = this
    fun release(): State<T> = this
    fun updateCache(emptyCache: Boolean): State<T> = this
    fun newPage(pageEmpty: Boolean): State<T> = this
    fun fail(error: Throwable): State<T> = this

}

class InitialState<T> : State<T> {

    override fun restart(): State<T> = InitialProgress()

    override fun refresh(): State<T> = InitialProgress()

    override fun release() = Released<T>()

}

class InitialProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {
        pageLoader.loadFirstPage()
    }

    override fun restart() = InitialProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        this
    } else {
        CachedData(true)
    }

    override fun fail(error: Throwable): State<T> = EmptyError(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()
}

class RestartProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {
        pageLoader.loadFirstPage()
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = EmptyError(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()

}

data class EmptyError<T>(val error: Throwable) : State<T> {

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun release() = Released<T>()

}

class EmptyData<T> : State<T> {

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()

}

data class CachedData<T>(val passiveProgress: Boolean,
                         val cachedThrowable: CachedThrowable? = null) : State<T> {

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> {
        return CachedData(false, CachedThrowable(error, this::class))
    }

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        CachedData(passiveProgress, cachedThrowable)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun release() = Released<T>()

}

data class Data<T>(val throwable: Throwable? = null) : State<T> {

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        Data(throwable)
    }


    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun loadNewPage() = PageProgress<T>()

    override fun release() = Released<T>()
}

data class Refresh<T> constructor(val previousState: State<T>) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {
        pageLoader.loadFirstPage()
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = when (previousState) {
        is EmptyError -> EmptyError(error)
        is EmptyData -> EmptyError(error)
        is CachedData -> CachedData(false, CachedThrowable(error, this::class))
        is AllData -> AllData(error)
        else -> Data(error)
    }

    override fun release() = Released<T>()

}

class PageProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {
        pageLoader.loadNextPage()
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        AllData()
    } else {
        Data()
    }

    override fun refresh() = Refresh(this)

    override fun fail(error: Throwable) = PageProgressFail<T>(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()

}

data class PageProgressFail<T>(val throwable: Throwable) : State<T> {

    override fun retry(): State<T> = PageProgress()

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun release() = Released<T>()
}

data class AllData<T>(val throwable: Throwable? = null) : State<T> {

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        AllData(throwable)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun release() = Released<T>()

}

class Released<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, cacheDataObserver: CacheDataObserver<T>) {
        pageLoader.release()
        cacheDataObserver.release()

    }

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()
}

data class CachedThrowable(val throwable: Throwable, val from: KClass<out State<*>>)