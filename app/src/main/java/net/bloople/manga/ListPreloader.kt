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
class ListPreloader<T>(
    private val context: Context,
    private val imageLoader: ImageLoader,
    private val preloadProvider: PreloadProvider<T>,
    private val maxPreload: Int
) : AbsListView.OnScrollListener {
    constructor(
        context: Context,
        preloadProvider: PreloadProvider<T>,
        maxPreload: Int) : this(context, context.imageLoader, preloadProvider, maxPreload)

    private val requestQueue = ArrayDeque<Disposable>(maxPreload + 1)

    private var lastEnd = 0
    private var lastStart = 0
    private var lastFirstVisible = -1
    private var totalItemCount = 0

    private var isIncreasing = true

    interface PreloadProvider<U> {
        fun getPreloadItems(position: Int): List<U?>

        fun getPreloadImageRequest(context: Context, item: U): ImageRequest?
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
                preloadAdapterPosition(preloadProvider.getPreloadItems(i), true)
            }
        }
        else {
            // Decreasing
            for(i in end - 1 downTo start) {
                preloadAdapterPosition(preloadProvider.getPreloadItems(i), false)
            }
        }

        lastStart = start
        lastEnd = end
    }

    private fun preloadAdapterPosition(items: List<T?>, isIncreasing: Boolean) {
        if(isIncreasing) {
            for(item in items) preloadItem(item)
        }
        else {
            for(item in items.reversed()) preloadItem(item)
        }
    }

    private fun preloadItem(item: T?) {
        if(item == null) return
        println($"Preloading item: $item")
        val imageRequest = preloadProvider.getPreloadImageRequest(context, item) ?: return

        val handle = imageLoader.enqueue(imageRequest)
        if(requestQueue.size >= maxPreload) requestQueue.removeFirst()
        requestQueue.add(handle)
    }

    private fun cancelAll() {
        for(handle in requestQueue) handle.dispose()
    }
}
