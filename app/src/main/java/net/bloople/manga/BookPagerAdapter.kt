package net.bloople.manga

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.bumptech.glide.RequestBuilder
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment

internal class BookPagerAdapter(fa: FragmentActivity?, private val book: Book) : FragmentStateAdapter(
    fa!!
), PreloadModelProvider<GlideUrl> {
    private var requestManager: RequestManager? = null
    private var preloader: RecyclerViewPreloader<GlideUrl>? = null

    override fun createFragment(i: Int): Fragment {
        return PageFragment.newInstance(book.pageUrl(i))
    }

    override fun getItemCount(): Int {
        return book.pages
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        requestManager = Glide.with(recyclerView)
        preloader = RecyclerViewPreloader(
            requestManager!!,
            this,
            ViewPreloadSizeProvider(recyclerView),
            ReadingSession.CACHE_PAGES_LIMIT
        )

        recyclerView.addOnScrollListener(preloader!!)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(preloader!!)
        preloader = null
        requestManager = null
    }

    override fun getPreloadItems(position: Int): List<GlideUrl> {
        val pageUrl = book.pageUrl(position)
        return listOf(pageUrl.toGlideUrl())
    }

    override fun getPreloadRequestBuilder(url: GlideUrl): RequestBuilder<Drawable> {
        return requestManager!!.load(url).transform(MatchWidthTransformation())
    }
}