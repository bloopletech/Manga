package net.bloople.manga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap

internal class BooksSorter {
    var sortMethod = SORT_AGE
    var sortDirectionAsc = false

    fun flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc
    }

    fun description(): String {
        return "Sorted by ${sortMethodDescription().lowercase(Locale.getDefault())} ${sortDirectionDescription().lowercase(Locale.getDefault())}"
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

    suspend fun sort(books: ArrayList<Book>, booksMetadata: HashMap<Long, BookMetadata>) {
        return withContext(Dispatchers.Default) {
            if(books.isEmpty()) return@withContext

            if(sortMethod == SORT_LAST_OPENED) {
                sortLastOpened(books, booksMetadata)
            }
            else if(sortMethod == SORT_OPENED_COUNT) {
                sortOpenedCount(books, booksMetadata)
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
    }

    private fun sortLastOpened(books: ArrayList<Book>, booksMetadata: HashMap<Long, BookMetadata>) {
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

    private fun sortOpenedCount(books: ArrayList<Book>, booksMetadata: HashMap<Long, BookMetadata>) {
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

    companion object {
        const val SORT_ALPHABETIC = 0
        const val SORT_AGE = 1
        const val SORT_LENGTH = 2
        const val SORT_LAST_OPENED = 3
        const val SORT_OPENED_COUNT = 4
        const val SORT_RANDOM = 5
    }
}