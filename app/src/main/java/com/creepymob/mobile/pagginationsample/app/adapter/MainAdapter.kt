package com.creepymob.mobile.pagginationsample.app.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.creepymob.mobile.pagginationsample.app.data.DisplayableItem
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager
import io.reactivex.Observer

/**
 * User: andrey
 * Date: 02.05.2018
 * Time: 23:48
 *
 */
class MainAdapter(onReloadClickObserver: Observer<Unit>, onLoadItemClickObserver: Observer<LoadItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegatesManager: AdapterDelegatesManager<List<DisplayableItem>> = AdapterDelegatesManager()

    var content: List<DisplayableItem>? = null
        set(value) {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        content?.get(oldItemPosition) == value?.get(newItemPosition)

                override fun getOldListSize(): Int = content?.size ?: 0

                override fun getNewListSize(): Int = value?.size ?: 0

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        content?.get(oldItemPosition) == value?.get(newItemPosition)
            })
            field = value
            result.dispatchUpdatesTo(this)
        }

    init {
        delegatesManager.addDelegate(NewPageProgressLoaderAdapter())
        delegatesManager.addDelegate(NewPageErrorLoadingAdapter(onReloadClickObserver))
        delegatesManager.addDelegate(LoadItemsAdapter(onLoadItemClickObserver))
    }

    override fun getItemViewType(position: Int): Int =
            delegatesManager.getItemViewType(content!!, position)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegatesManager.onCreateViewHolder(parent, viewType)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(content!!, position, holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        delegatesManager.onBindViewHolder(content!!, position, holder, payloads)
    }

    override fun getItemCount(): Int = content?.size ?: 0


}