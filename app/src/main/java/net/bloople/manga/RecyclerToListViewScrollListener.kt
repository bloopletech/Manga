package net.bloople.manga

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

// Based on https://github.com/bumptech/glide/blob/b12f574fd6ea20430c55c5a2eb29d624d843bf3e/integration/recyclerview/src/main/java/com/bumptech/glide/integration/recyclerview/RecyclerToListViewScrollListener.java
class RecyclerToListViewScrollListener(
    private val scrollListener: AbsListView.OnScrollListener) : RecyclerView.OnScrollListener() {
    private var lastFirstVisible = -1
    private var lastVisibleCount = -1
    private var lastItemCount = -1

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val listViewState = when(newState) {
            RecyclerView.SCROLL_STATE_DRAGGING -> AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
            RecyclerView.SCROLL_STATE_IDLE -> AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            RecyclerView.SCROLL_STATE_SETTLING -> AbsListView.OnScrollListener.SCROLL_STATE_FLING
            else -> return
        }

        scrollListener.onScrollStateChanged(null, listViewState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val visibleCount = abs(firstVisible - layoutManager.findLastVisibleItemPosition())
        val itemCount = recyclerView.adapter!!.itemCount

        if(firstVisible != lastFirstVisible || visibleCount != lastVisibleCount || itemCount != lastItemCount) {
            scrollListener.onScroll(null, firstVisible, visibleCount, itemCount)
            lastFirstVisible = firstVisible
            lastVisibleCount = visibleCount
            lastItemCount = itemCount
        }
    }
}
