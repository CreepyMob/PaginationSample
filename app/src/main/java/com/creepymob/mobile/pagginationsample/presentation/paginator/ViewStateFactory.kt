package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 07.05.2018
 * Time: 22:58
 *
 */
class ViewStateFactory<T>(private val contentStore: ContentStore<T>) {

    /*fun create(state: State<T>): ViewState<T>? = when (state) {
        is InitialState<T> -> null
        is InitialProgress<T> -> ViewState.EmptyLoadingViewState()
        is RestartProgress<T> -> ViewState.EmptyLoadingViewState()
        is EmptyError<T> -> ViewState.EmptyListErrorViewState(state.error)
        is EmptyData<T> -> ViewState.EmptyContentViewState()
        is CachedData<T> -> ViewState.ContentViewState(contentStore.content,
                isPassiveProgress = state.passiveProgress,
                contentThrowable = state.contentThrowable)
        is Data<T> -> ViewState.ContentViewState(contentStore.content,
                contentThrowable = state.contentThrowable)
        is Refresh<T> -> create(state.previousState)
        is PageProgress<T> -> ViewState.ContentViewState(contentStore.content, isNextPageLoaded = true)
        is PageProgressFail<T> -> ViewState.ContentViewState(contentStore.content,
        contentThrowable = ContentThrowable(state.throwable, whenNextPageLoaded = true))
        is AllData<T>->
    }*/
}