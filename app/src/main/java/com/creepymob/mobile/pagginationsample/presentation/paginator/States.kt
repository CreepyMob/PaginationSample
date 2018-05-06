package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:47
 *
 */

interface State<T> {

    fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>?
    fun restart(): State<T> = this
    fun refresh(): State<T> = this
    fun retry(): State<T> = this
    fun loadNewPage(): State<T> = this
    fun release(): State<T> = this
    fun updateCache(emptyCache: Boolean): State<T> = this
    fun newPage(pageEmpty: Boolean): State<T> = this
    fun fail(error: Throwable): State<T> = this

}

class EmptyState<T> : State<T> {

    private var hasNotEmptyCache: Boolean = false

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? = null


    override fun restart(): State<T> = InitialProgress()

    override fun refresh(): State<T> = if (hasNotEmptyCache) {
        CachedData(true)
    } else {
        InitialProgress()
    }

    override fun updateCache(emptyCache: Boolean): State<T> {
        hasNotEmptyCache = !emptyCache
        return super.updateCache(emptyCache)
    }

    override fun release() = Released<T>()

}

class InitialProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T> {

        pageLoader.loadFirstPage()
        return ViewState.EmptyLoadingViewState()
    }

    override fun restart() = this

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

    override fun hashCode(): Int  =  javaClass.hashCode()
}

class RestartProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T> {

        pageLoader.loadFirstPage()
        return ViewState.EmptyLoadingViewState()
    }

    override fun restart() = this

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = EmptyError(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int  =  javaClass.hashCode()


}

class EmptyError<T>(private val error: Throwable) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        return ViewState.EmptyListErrorViewState(error)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = EmptyErrorRefresh<T>(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int  =  javaClass.hashCode()
}

data class EmptyErrorRefresh<T>(private val error: Throwable) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        pageLoader.loadFirstPage()
        return ViewState.EmptyListErrorViewState(error, true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = EmptyError(error)

    override fun release() = Released<T>()

}

class EmptyData<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        return ViewState.EmptyContentViewState()
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = EmptyDataRefresh<T>()

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int  =  javaClass.hashCode()

}

class EmptyDataRefresh<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        pageLoader.loadFirstPage()
        return ViewState.EmptyContentViewState(true)
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

    override fun hashCode(): Int  =  javaClass.hashCode()
}

data class CachedData<T>(private val passiveProgress: Boolean,
                    private var throwable: Throwable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        return ViewState.ContentViewState(pageLoader.content, isPassiveProgress = passiveProgress, contentThrowable = throwable?.let { ContentThrowable(it, whenRefresh = true) })
    }

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun updateCache(emptyCache: Boolean): State<T> = CachedData(passiveProgress, throwable)

    override fun restart() = RestartProgress<T>()

    override fun refresh() = CachedRefresh<T>()

    override fun release() = Released<T>()

}

data class Data<T>(val contentThrowable: ContentThrowable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        return ViewState.ContentViewState(pageLoader.content,
                contentThrowable = contentThrowable)
    }

    override fun updateCache(emptyCache: Boolean): State<T> = Data(contentThrowable)

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>()

    override fun loadNewPage() = PageProgress<T>()

    override fun release() = Released<T>()
}

data class Refresh<T>(private val reachAllData: Boolean = false) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        pageLoader.loadFirstPage()
        return ViewState.ContentViewState(pageLoader.content, isRefresh = true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = if (reachAllData) {
        AllData(error)
    } else {
        Data(contentThrowable = ContentThrowable(error, whenRefresh = true))
    }

    override fun release() = Released<T>()

}

class CachedRefresh<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        pageLoader.loadFirstPage()
        return ViewState.ContentViewState(pageLoader.content, isRefresh = true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = CachedData(false, error)

    override fun release() = Released<T>()

}

class PageProgress<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        pageLoader.loadNextPage()
        return ViewState.ContentViewState(pageLoader.content, isNextPageLoaded = true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        AllData()
    } else {
        Data()
    }

    override fun refresh() = Refresh<T>()

    override fun fail(error: Throwable) = PageProgressFail<T>(error)

    override fun release() = Released<T>()

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int  =  javaClass.hashCode()

}

data class PageProgressFail<T>(private val throwable: Throwable) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {

        return ViewState.ContentViewState(pageLoader.content,
                contentThrowable = ContentThrowable(throwable, whenNextPageLoaded = true))
    }

    override fun retry(): State<T> = PageProgress()

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>()

    override fun release() = Released<T>()
}

data class AllData<T>(private val throwable: Throwable? = null) : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        return ViewState.ContentViewState(pageLoader.content, contentThrowable = throwable?.let { ContentThrowable(throwable, whenRefresh = true) })
    }

    override fun updateCache(emptyCache: Boolean): State<T> = AllData(throwable)

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(true)

    override fun release() = Released<T>()

}

class Released<T> : State<T> {

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        pageLoader.release()
        return null
    }

    override fun equals(other: Any?): Boolean = other != null && this::class == other::class

    override fun hashCode(): Int  =  javaClass.hashCode()
}