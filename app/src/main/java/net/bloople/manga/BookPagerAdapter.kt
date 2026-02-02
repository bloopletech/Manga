package net.bloople.manga

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BookPagerAdapter(fa: FragmentActivity, private val book: Book) : FragmentStateAdapter(fa) {
    override fun createFragment(i: Int) = PageFragment.newInstance(book.pageUrl(i))
    override fun getItemCount(): Int = book.pages
}