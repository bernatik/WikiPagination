package com.vironit.test.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vironit.test.data.api.ApiFactory

abstract class PagesScrollListener(val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    /*
    *  Page size is a limit for pages loaded from API
    */

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemsCount = layoutManager.childCount
        val totalItemsCount = layoutManager.itemCount
        val firstItemVisiblePos = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemsCount + firstItemVisiblePos) >= totalItemsCount ||
                firstItemVisiblePos >= 0 ||
                totalItemsCount >= ApiFactory.PAGE_LIMIT_SIZE
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean
}