package net.bloople.manga

import android.content.Context
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.*

internal class BooksSorter {
    var sortMethod = SORT_AGE
    var sortDirectionAsc = false

    fun flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc
    }

    fun description(): String {
        return "Sorted by " + sortMethodDescription().toLowerCase() + " " + sortDirectionDescription().toLowerCase()
    }

    private fun sortMethodDescription(): String {
        return when(sortMethod) {
            SORT_ALPHABETIC -> "Title"
            SORT_AGE -> "Published Date"
            SORT_LENGTH -> "Page Count"
            SORT_LAST_OPENED -> "Last Opened At"
            SORT_OPENED_COUNT -> "Opened Count"
            SORT_RANDOM -> "Random"
            else -> throw IllegalStateException("sort_method not in valid range")
        }
    }

    private fun sortDirectionDescription(): String {
        return if(sortDirectionAsc) "Ascending" else "Descending"
    }

    fun sort(context: Context, books: ArrayList<Book>) {
        if(books.isEmpty()) return

        if(sortMethod == SORT_LAST_OPENED) {
            sortLastOpened(context, books)
        }
        else if(sortMethod == SORT_OPENED_COUNT) {
            sortOpenedCount(context, books)
        }
        else if(sortMethod == SORT_RANDOM) {
            sortRandom(books)
        }
        else {
            books.sortWith { a: Book, b: Book ->
                return@sortWith when(sortMethod) {
                    SORT_ALPHABETIC -> a.normalisedTitle.compareTo(b.normalisedTitle)
                    SORT_AGE -> a.publishedOn.compareTo(b.publishedOn)
                    SORT_LENGTH -> a.pages.compareTo(b.pages)
                    else -> 0
                }
            }
            if(!sortDirectionAsc) books.reverse()
        }
    }

    private fun sortLastOpened(context: Context, books: ArrayList<Book>) {
        val booksMetadata = metadataForBooks(context, books)
        books.sortWith { a: Book, b: Book ->
            val abm = booksMetadata[a.id]
            val bbm = booksMetadata[b.id]

            if(abm == null && bbm == null) return@sortWith 0
            if(abm == null) return@sortWith 1
            if(bbm == null) return@sortWith -1
            if(sortDirectionAsc) return@sortWith abm.lastOpenedAt.compareTo(bbm.lastOpenedAt)
            else return@sortWith bbm.lastOpenedAt.compareTo(abm.lastOpenedAt)
        }
    }

    private fun sortOpenedCount(context: Context, books: ArrayList<Book>) {
        val booksMetadata = metadataForBooks(context, books)
        books.sortWith { a: Book, b: Book ->
            val abm = booksMetadata[a.id]
            val bbm = booksMetadata[b.id]

            if(abm == null && bbm == null) return@sortWith 0
            if(abm == null) return@sortWith 1
            if(bbm == null) return@sortWith -1
            if(sortDirectionAsc) return@sortWith abm.openedCount.compareTo(bbm.openedCount)
            else return@sortWith bbm.openedCount.compareTo(abm.openedCount)
        }
    }

    private fun sortRandom(books: ArrayList<Book>) {
        val seed = System.currentTimeMillis() / (1000L * 60L * 30L)
        books.shuffle(Random(seed))
        if(!sortDirectionAsc) books.reverse()
    }

    private fun metadataForBooks(context: Context, books: ArrayList<Book>): HashMap<Long, BookMetadata> {
        val db = DatabaseHelper.instance(context)
        val sb = StringBuilder()
        for(b in books) {
            if(sb.isNotEmpty()) sb.append(",")
            sb.append(b.id)
        }

        db.rawQuery("SELECT * FROM books_metadata WHERE book_id IN ($sb)", arrayOf()).use {
            val booksMetadata = HashMap<Long, BookMetadata>()
            while(it.moveToNext()) {
                val bookMetadata = BookMetadata(it)
                booksMetadata[bookMetadata.bookId] = bookMetadata
            }
            return booksMetadata
        }
    }

    companion object {
        const val SORT_ALPHABETIC = 0
        const val SORT_AGE = 1
        const val SORT_LENGTH = 2
        const val SORT_LAST_OPENED = 3
        const val SORT_OPENED_COUNT = 4
        const val SORT_RANDOM = 5
    }
}