package com.creepymob.mobile.pagginationsample.app

import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.presentation.paginator.ViewState
import io.reactivex.Observable

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 1:39
 *
 */
interface RegularMviListView<T> {

    val loadMoreEvent: Observable<Unit>

    val refreshEvent: Observable<Unit>

    val filterEvent: Observable<DataLoadFilter>

    val hardReloadEvent: Observable<Unit>

    val reloadPageEvent: Observable<Unit>

    fun render(listViewState: ViewState<T>)
}