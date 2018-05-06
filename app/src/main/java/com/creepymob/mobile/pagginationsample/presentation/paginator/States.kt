package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:47
 *
 */

interface State<T> {

    fun invoked(): Boolean
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

data class EmptyState<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return null
    }

    override fun refresh(): State<T> = InitialProgress()

    override fun release() = Released<T>()

}

data class InitialProgress<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T> {
        invoked = true
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

}

data class RestartProgress<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T> {
        invoked = true
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

}

data class EmptyError<T>(private val error: Throwable, private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return ViewState.EmptyListErrorViewState(error)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = EmptyErrorRefresh<T>(error)

    override fun release() = Released<T>()
}

data class EmptyErrorRefresh<T>(private val error: Throwable, private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
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

data class EmptyData<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return ViewState.EmptyContentViewState()
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = EmptyDataRefresh<T>()

    override fun release() = Released<T>()

}

data class EmptyDataRefresh<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
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

}

data class CachedData<T>(private val passiveProgress: Boolean,
                         private var throwable: Throwable? = null,
                         private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return ViewState.ContentViewState(pageLoader.content, isPassiveProgress = passiveProgress, contentThrowable = throwable?.let { ContentThrowable(it, whenRefresh = true) })
    }

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = CachedRefresh<T>()

    override fun release() = Released<T>()

}

data class Data<T>(val contentThrowable: ContentThrowable? = null,
                   private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return ViewState.ContentViewState(pageLoader.content,
                contentThrowable = contentThrowable)
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>()

    override fun loadNewPage() = PageProgress<T>()

    override fun release() = Released<T>()
}

data class Refresh<T>(private val reachAllData: Boolean = false, private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
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

data class CachedRefresh<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        pageLoader.loadFirstPage()
        return ViewState.ContentViewState(pageLoader.content, isRefresh = true)
    }

    override fun restart() = RestartProgress<T>()

    override fun newPage(pageEmpty: Boolean): State<T> = if (pageEmpty) {
        EmptyData()
    } else {
        Data()
    }

    override fun fail(error: Throwable): State<T> = CachedData<T>(false, error)


    override fun release() = Released<T>()

}

data class PageProgress<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
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
}

data class PageProgressFail<T>(private val throwable: Throwable, private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        return ViewState.ContentViewState(pageLoader.content,
                contentThrowable = ContentThrowable(throwable, whenNextPageLoaded = true))
    }

    override fun retry(): State<T> = PageProgress<T>()

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>()

    override fun release() = Released<T>()
}

data class AllData<T>(private val throwable: Throwable? = null, private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true

        return ViewState.ContentViewState(pageLoader.content, contentThrowable = throwable?.let { ContentThrowable(throwable, whenRefresh = true) })
    }

    override fun restart() = RestartProgress<T>()

    override fun refresh() = Refresh<T>(true)

    override fun release() = Released<T>()

}

data class Released<T>(private var invoked: Boolean = false) : State<T> {

    override fun invoked(): Boolean = invoked

    override fun invoke(pageLoader: PageContentLoader<T>): ViewState<T>? {
        invoked = true
        pageLoader.release()
        return null
    }

}