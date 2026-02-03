package net.bloople.manga

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil3.request.ImageRequest

class BookPagerAdapter(
    fa: FragmentActivity,
    private val book: Book) : FragmentStateAdapter(fa), ListPreloader.Provider {
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

    override fun getPreloadRequests(context: Context, position: Int): List<ImageRequest> {
        val pageUrl = book.pageUrl(position)
        return listOf(ImageRequest.Builder(context).data(pageUrl.build()).build())
    }
}