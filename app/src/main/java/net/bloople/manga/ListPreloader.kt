package net.bloople.manga

import android.content.Context
import android.widget.AbsListView
import coil3.ImageLoader
import coil3.imageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import kotlin.math.max
import kotlin.math.min

// Based on https://github.com/bumptech/glide/blob/b12f574fd6ea20430c55c5a2eb29d624d843bf3e/library/src/main/java/com/bumptech/glide/ListPreloader.java#L29
open class ListPreloader(
    private val context: Context,
    private val imageLoader: ImageLoader,
    private val preloadProvider: Provider,
    private val maxPreload: Int) : AbsListView.OnScrollListener {
    constructor(
        context: Context,
        provider: Provider,
        maxPreload: Int) : this(context, context.imageLoader, provider, maxPreload)

    private val requestQueue = ArrayDeque<Disposable>(maxPreload + 1)

    private var lastEnd = 0
    private var lastStart = 0
    private var lastFirstVisible = -1
    private var totalItemCount = 0

    private var isIncreasing = true

    interface Provider {
        fun getPreloadRequests(context: Context, position: Int): List<ImageRequest>
    }

    override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
        // Do nothing.
    }

    override fun onScroll(absListView: AbsListView?, firstVisible: Int, visibleCount: Int, totalCount: Int) {
        if(totalItemCount == 0 && totalCount == 0) return
        totalItemCount = totalCount
        if(firstVisible > lastFirstVisible) {
            preload(firstVisible + visibleCount, true)
        }
        else if(firstVisible < lastFirstVisible) {
            preload(firstVisible, false)
        }
        lastFirstVisible = firstVisible
    }

    private fun preload(start: Int, increasing: Boolean) {
        if(isIncreasing != increasing) {
            isIncreasing = increasing
            cancelAll()
        }
        preload(start, start + (if(increasing) maxPreload else -maxPreload))
    }

    private fun preload(from: Int, to: Int) {
        var start: Int
        var end: Int
        if(from < to) {
            start = max(lastEnd, from)
            end = to
        }
        else {
            start = to
            end = min(lastStart, from)
        }
        end = min(totalItemCount, end)
        start = min(totalItemCount, max(0, start))

        if(from < to) {
            // Increasing
            for(i in start ..< end) {
                preloadAdapterPosition(i, true)
            }
        }
        else {
            // Decreasing
            for(i in end - 1 downTo start) {
                preloadAdapterPosition(i, false)
            }
        }

        lastStart = start
        lastEnd = end
    }

    private fun preloadAdapterPosition(position: Int, isIncreasing: Boolean) {
        val requests = preloadProvider.getPreloadRequests(context, position)

        if(isIncreasing) {
            for(request in requests) preloadRequest(request)
        }
        else {
            for(request in requests.asReversed()) preloadRequest(request)
        }
    }

    private fun preloadRequest(request: ImageRequest) {
        val handle = imageLoader.enqueue(request)
        if(requestQueue.size >= maxPreload) requestQueue.removeFirst()
        requestQueue.add(handle)
    }

    private fun cancelAll() {
        for(handle in requestQueue) handle.dispose()
    }
}
