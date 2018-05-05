package com.creepymob.mobile.pagginationsample.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.creepymob.mobile.pagginationsample.R
import com.creepymob.mobile.pagginationsample.app.data.DisplayableItem
import com.creepymob.mobile.pagginationsample.app.inflate
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import kotlinx.android.synthetic.main.cell_load_items.view.*

/**
 * User: andrey
 * Date: 03.05.2018
 * Time: 0:17
 *
 */
class LoadItemsAdapter(private val onLoadItemClickObserver: Observer<LoadItem>) : AbsListItemAdapterDelegate<LoadItem, DisplayableItem, LoadItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): LoadItemsViewHolder =
            LoadItemsViewHolder(parent.inflate(R.layout.cell_load_items), onLoadItemClickObserver)

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean =
            item is LoadItem

    override fun onBindViewHolder(item: LoadItem, viewHolder: LoadItemsViewHolder, payloads: MutableList<Any>) {
        viewHolder.bind(item)
    }
}

class LoadItemsViewHolder(view: View, private val onLoadItemClickObserver: Observer<LoadItem>) : RecyclerView.ViewHolder(view) {

    private var item: LoadItem? = null

    init {
        RxView.clicks(view)
                .filter { item != null }
                .map { item }
                .subscribe { onLoadItemClickObserver }
    }

    fun bind(item: LoadItem) {
        this.item = item

        itemView.title.text = item.title
        itemView.message.text = item.title
    }
}