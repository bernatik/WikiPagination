package com.vironit.test.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vironit.test.R
import com.vironit.test.data.model.UiPage

class PagesAdapter : RecyclerView.Adapter<PagesAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_DATA = 1
    }

    private var isLoading = false

    private var pages: MutableList<UiPage> = mutableListOf()

    fun addPages(newPages: List<UiPage>) {
        pages.addAll(newPages)
        notifyItemInserted(pages.size - 1)
    }

    fun addLoadingItem() {
        isLoading = true
        pages.add(UiPage("", 0))
    }

    fun removeLoadingItem() {
        isLoading = false
        val position = pages.size - 1
        val lastItem = getItem(position)
        lastItem?.let {
            pages.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear(){
        pages.clear()
    }

    private fun getItem(position: Int): UiPage? {
        if (position < 0 || pages.size == 0) return null
        return pages[position]
    }

    override fun getItemViewType(position: Int): Int =
        when (isLoading) {
            true -> {
                if (position == pages.size - 1) {
                    VIEW_TYPE_LOADING
                } else {
                    VIEW_TYPE_DATA
                }
            }
            false -> {
                VIEW_TYPE_DATA
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            VIEW_TYPE_DATA -> {
                ViewHolder.Page(LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false))
            }
            VIEW_TYPE_LOADING -> {
                ViewHolder.Loading(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
            }
            else -> {
                ViewHolder.Page(LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false))
            }
        }


    override fun getItemCount(): Int {
        return pages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder.Page -> {
                val page = pages[holder.adapterPosition]
                holder.pageTitle.text = page.title
                holder.imageNumber.text = holder.imageNumber.context.resources.getQuantityString(
                    R.plurals.image_plurals,
                    page.imagesCount,
                    page.imagesCount
                )
            }
            is ViewHolder.Loading -> {

            }
        }
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class Page(itemView: View) : ViewHolder(itemView) {
            val pageTitle: TextView = itemView.findViewById(R.id.tv_title)
            val imageNumber: TextView = itemView.findViewById(R.id.tv_image_count)
        }

        class Loading(itemView: View) : ViewHolder(itemView)
    }


}