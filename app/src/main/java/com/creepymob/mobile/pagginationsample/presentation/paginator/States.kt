package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:47
 *
 */

interface State<T> {

    fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>?
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

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? = null

    override fun restart(): State<T> = InitialProgress()

    override fun refresh(): State<T> = InitialProgress()

    override fun release() = Released<T>()

}

class InitialProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T> {

        pageLoader.loadFirstPage()
        return ViewState.EmptyLoadingViewState()
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

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T> {

        pageLoader.loadFirstPage()
        return ViewState.EmptyLoadingViewState()
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

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {

        return ViewState.EmptyListErrorViewState(error)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(this)

    override fun release() = Released<T>()

}

class EmptyData<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {
        return ViewState.EmptyContentViewState()
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(this)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()

}

data class CachedData<T>(val passiveProgress: Boolean,
                         val contentThrowable: ContentThrowable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {
        return ViewState.ContentViewState(contentStore.content,
                isPassiveProgress = passiveProgress,
                contentThrowable = contentThrowable)
    }


    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> {
        return CachedData(false, ContentThrowable(error))
    }

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        CachedData(passiveProgress, contentThrowable)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(this)

    override fun release() = Released<T>()

}

data class Data<T>(val contentThrowable: ContentThrowable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {

        return ViewState.ContentViewState(contentStore.content,
                contentThrowable = contentThrowable)
    }

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        Data(contentThrowable)
    }


    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh(this)

    override fun loadNewPage() = PageProgress<T>()

    override fun release() = Released<T>()
}

data class Refresh<T> constructor(val previousState: State<T>) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {
        pageLoader.loadFirstPage()
        return when (previousState) {
            is EmptyError -> ViewState.EmptyListErrorViewState(previousState.error, true)
            is EmptyData -> ViewState.EmptyContentViewState(true)
            is CachedData -> ViewState.ContentViewState(contentStore.content, isPassiveProgress = true)
            else -> ViewState.ContentViewState(contentStore.content, isRefresh = true)
        }
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
        is CachedData -> CachedData(false, ContentThrowable(error, whenRefresh = true))
        else -> Data(ContentThrowable(error, whenRefresh = true))
    }

    override fun release() = Released<T>()

}

class PageProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {

        pageLoader.loadNextPage()
        return ViewState.ContentViewState(contentStore.content, isNextPageLoaded = true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        AllData()
    } else {
        Data()
    }

    override fun refresh() = Refresh<T>(this)

    override fun fail(error: Throwable) = PageProgressFail<T>(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()

}

data class PageProgressFail<T>(val throwable: Throwable) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>,
                        contentStore: ContentStore<T>,
                        cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {

        return ViewState.ContentViewState(contentStore.content,
                contentThrowable = ContentThrowable(throwable, whenNextPageLoaded = true))
    }

    override fun retry(): State<T> = PageProgress()

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(this)

    override fun release() = Released<T>()
}

data class AllData<T>(val throwable: Throwable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {
        return ViewState.ContentViewState(contentStore.content, contentThrowable = throwable?.let { ContentThrowable(throwable, whenRefresh = true) })
    }

    override fun updateCache(emptyCache: Boolean): State<T> = if (emptyCache) {
        EmptyData()
    } else {
        AllData(throwable)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(this)

    override fun release() = Released<T>()

}

class Released<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>, contentStore: ContentStore<T>, cacheDataObserver: CacheDataObserver<T>): ViewState<T>? {
        pageLoader.release()
        cacheDataObserver.release()
        return null
    }

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int = javaClass.hashCode()
}

class StateThrowable<T>(throwable: Throwable, from: State<T>)