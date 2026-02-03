package net.bloople.manga

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil3.request.ImageRequest

class BookPagerAdapter(
    fa: FragmentActivity,
    private val book: Book) : FragmentStateAdapter(fa), ListPreloader.PreloadProvider<MangosUrl> {
    private val preloader = RecyclerViewPreloader(fa, this, ReadingSession.CACHE_PAGES_LIMIT)

    override fun createFragment(i: Int) = PageFragment.newInstance(book.pageUrl(i))
    override fun getItemCount(): Int = book.pages

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(preloader)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(preloader)
    }

    override fun getPreloadItems(position: Int): List<MangosUrl> {
        val pageUrl = book.pageUrl(position)
        return listOf(pageUrl)
    }

    override fun getPreloadImageRequest(context: Context, item: MangosUrl): ImageRequest {
        return ImageRequest.Builder(context).data(item.build()).build()
    }
}