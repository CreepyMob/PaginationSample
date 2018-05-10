package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 07.05.2018
 * Time: 22:58
 *
 */
//TODO test for
class ViewStateFactory<T>(private val contentStore: ContentStore<T>,
                          private val viewStateCachedDataFactory: CachedDataViewStateFactoryDelegate<T> = CachedDataViewStateFactoryDelegate(contentStore),
                          private val refreshViewStateFactoryDelegate: RefreshViewStateFactoryDelegate<T> = RefreshViewStateFactoryDelegate(contentStore)) {

    fun create(state: State<T>): ViewState<T>? = when (state) {
        is InitialState<T> -> null
        is InitialProgress<T> -> ViewState.EmptyLoadingViewState()
        is RestartProgress<T> -> ViewState.EmptyLoadingViewState()
        is EmptyError<T> -> ViewState.EmptyListErrorViewState(state.error)
        is EmptyData<T> -> ViewState.EmptyContentViewState()
        is CachedData<T> -> {
            viewStateCachedDataFactory.create(state)
        }
        is Data<T> -> {
            ViewState.ContentViewState(contentStore.content,
                    contentThrowable = state.throwable?.let { ContentThrowable(state.throwable, whenRefresh = true) })
        }
        is Refresh<T> -> {
            refreshViewStateFactoryDelegate.create(state)
        }
        is PageProgress<T> -> ViewState.ContentViewState(contentStore.content, isNextPageLoaded = true)
        is PageProgressFail<T> -> ViewState.ContentViewState(contentStore.content,
                contentThrowable = ContentThrowable(state.throwable, whenNextPageLoaded = true))
        is AllData<T> -> ViewState.ContentViewState(contentStore.content,
                contentThrowable = state.throwable?.let { ContentThrowable(it, whenRefresh = true) })
        is Released<T> -> null
        else -> throw RuntimeException("unknown state")
    }
}

class CachedDataViewStateFactoryDelegate<T>(private val contentStore: ContentStore<T>) {

    fun create(state: CachedData<T>): ViewState<T> {
        val contentThrowable = when {
            state.cachedThrowable == null -> null
            state.cachedThrowable.from == Refresh::class -> ContentThrowable(state.cachedThrowable.throwable, whenRefresh = true)
            else -> ContentThrowable(state.cachedThrowable.throwable, whenRefresh = true)
        }

        return ViewState.ContentViewState(contentStore.content,
                isPassiveProgress = state.passiveProgress,
                contentThrowable = contentThrowable)
    }
}

class RefreshViewStateFactoryDelegate<T>(private val contentStore: ContentStore<T>) {

    fun create(state: Refresh<T>): ViewState<T> = when (state.previousState) {
        is EmptyError -> ViewState.EmptyListErrorViewState(state.previousState.error, true)
        is EmptyData -> ViewState.EmptyContentViewState(true)
        is CachedData -> ViewState.ContentViewState(contentStore.content, isPassiveProgress = true)
        else -> ViewState.ContentViewState(contentStore.content, isRefresh = true)
    }
}