package net.bloople.manga

import androidx.viewpager2.widget.ViewPager2
import net.bloople.manga.audit.BooksAuditor
import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

class ReadingSession(private val library: Library, private val book: Book) {
    private var pager: ViewPager2? = null
    private val metadata: BookMetadata = BookMetadata.findOrCreateByBookId(book.id)
    private val auditor = BooksAuditor()

    fun start() {
        metadata.lastOpenedAt = System.currentTimeMillis()
        metadata.openedCount++
        metadata.save()
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
        metadata.lastReadPosition = page
        metadata.save()
    }

    fun resume() {
        page(metadata.lastReadPosition)
    }

    fun finish() {
        if(page() == book.pages - 1) bookmark(0)
        auditor.closed(library, book, page())
    }

    companion object {
        const val CACHE_PAGES_LIMIT = 2
    }
}