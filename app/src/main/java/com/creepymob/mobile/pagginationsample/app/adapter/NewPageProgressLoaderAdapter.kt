package com.creepymob.mobile.pagginationsample.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.creepymob.mobile.pagginationsample.R
import com.creepymob.mobile.pagginationsample.app.data.DisplayableItem
import com.creepymob.mobile.pagginationsample.app.data.NewPageProgressLoader
import com.creepymob.mobile.pagginationsample.app.inflate
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

/**
 * User: andrey
 * Date: 02.05.2018
 * Time: 23:55
 *
 */
class NewPageProgressLoaderAdapter: AbsListItemAdapterDelegate<NewPageProgressLoader, DisplayableItem, NewPageLoaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): NewPageLoaderViewHolder =
            NewPageLoaderViewHolder(parent.inflate(R.layout.cell_new_page_progress_loader))

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean =
            item is NewPageProgressLoader

    override fun onBindViewHolder(item: NewPageProgressLoader, viewHolder: NewPageLoaderViewHolder, payloads: MutableList<Any>) {

    }
}


class NewPageLoaderViewHolder(view: View): RecyclerView.ViewHolder(view)