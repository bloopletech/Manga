package net.bloople.manga

import androidx.viewpager2.widget.ViewPager2
import net.bloople.manga.audit.BooksAuditor
import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

internal class ReadingSession(private val context: Context, private val library: Library, private val book: Book) {
    private var pager: ViewPager2? = null
    private val metadata: BookMetadata = BookMetadata.findOrCreateByBookId(context, book.id)
    private val auditor: BooksAuditor = BooksAuditor(context)

    fun start() {
        metadata.lastOpenedAt(System.currentTimeMillis())
        metadata.openedCount(metadata.openedCount() + 1)
        metadata.save(context)
        auditor.opened(library, book, page())
    }

    @SuppressLint("WrongConstant")
    fun bind(fa: FragmentActivity, pager: ViewPager2) {
        this.pager = pager

        pager.adapter = BookPagerAdapter(fa, book)

        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bookmark(position)
            }
        })

        pager.offscreenPageLimit = CACHE_PAGES_LIMIT
    }

    fun page(): Int {
        return pager!!.currentItem
    }

    fun page(page: Int) {
        pager!!.setCurrentItem(page, false)
    }

    fun go(change: Int) {
        pager!!.setCurrentItem(pager!!.currentItem + change, false)
    }

    private fun bookmark(page: Int) {
        metadata.lastReadPosition(page)
        metadata.save(context)
    }

    fun resume() {
        page(metadata.lastReadPosition())
    }

    fun finish() {
        if(page() == book.pages - 1) bookmark(0)
        auditor.closed(library, book, page())
    }

    companion object {
        const val CACHE_PAGES_LIMIT = 2
    }
}