package com.creepymob.mobile.pagginationsample.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.creepymob.mobile.pagginationsample.R
import com.creepymob.mobile.pagginationsample.app.data.DisplayableItem
import com.creepymob.mobile.pagginationsample.app.data.NewPageErrorLoading
import com.creepymob.mobile.pagginationsample.app.inflate
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import kotlinx.android.synthetic.main.cell_new_page_error_loading.view.*

/**
 * User: andrey
 * Date: 02.05.2018
 * Time: 23:55
 *
 */
class NewPageErrorLoadingAdapter(private val onReloadClickObserver: Observer<Unit>) : AbsListItemAdapterDelegate<NewPageErrorLoading, DisplayableItem, NewPageErrorLoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): NewPageErrorLoadingViewHolder =
            NewPageErrorLoadingViewHolder(parent.inflate(R.layout.cell_new_page_error_loading), onReloadClickObserver)

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean =
            item is NewPageErrorLoading

    override fun onBindViewHolder(item: NewPageErrorLoading, viewHolder: NewPageErrorLoadingViewHolder, payloads: MutableList<Any>) {

    }
}

class NewPageErrorLoadingViewHolder(view: View, onReloadClickObserver: Observer<Unit>) : RecyclerView.ViewHolder(view) {

    init {
        RxView.clicks(itemView.reloadButton)
                .map { Unit }
                .subscribe(onReloadClickObserver)
    }

}

