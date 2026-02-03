package net.bloople.manga

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.imageLoader
import net.bloople.manga.ListPreloader.PreloadProvider

// Based on https://github.com/bumptech/glide/blob/b12f574fd6ea20430c55c5a2eb29d624d843bf3e/integration/recyclerview/src/main/java/com/bumptech/glide/integration/recyclerview/RecyclerViewPreloader.java
class RecyclerViewPreloader<T>(
    imageLoader: ImageLoader,
    preloadProvider: PreloadProvider<T>,
    maxPreload: Int
) : RecyclerView.OnScrollListener() {
    private val recyclerScrollListener = RecyclerToListViewScrollListener(
        ListPreloader(imageLoader, preloadProvider, maxPreload))

    constructor(
        context: Context,
        preloadProvider: PreloadProvider<T>,
        maxPreload: Int
    ) : this(context.imageLoader, preloadProvider, maxPreload)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        recyclerScrollListener.onScrolled(recyclerView, dx, dy)
    }
}
