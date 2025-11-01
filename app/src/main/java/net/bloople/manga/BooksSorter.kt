package net.bloople.manga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

internal class BooksSorter {
    var sortMethod = BooksSortMethod.SORT_AGE
    var sortDirectionAsc = false

    fun flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc
    }

    val sortMethodDescription: String
        get() = when(sortMethod) {
            BooksSortMethod.SORT_ALPHABETIC -> "Title"
            BooksSortMethod.SORT_AGE -> "Published Date"
            BooksSortMethod.SORT_LENGTH -> "Page Count"
            BooksSortMethod.SORT_LAST_OPENED -> "Last Opened At"
            BooksSortMethod.SORT_OPENED_COUNT -> "Opened Count"
            BooksSortMethod.SORT_RANDOM -> "Random"
        }

    val sortDirectionDescription: String
        get() = if(sortDirectionAsc) "Ascending" else "Descending"

    suspend fun sort(books: ArrayList<Book>, booksMetadata: HashMap<Long, BookMetadata>) {
        return withContext(Dispatchers.Default) {
            if(books.isEmpty()) return@withContext

            if(sortMethod == BooksSortMethod.SORT_LAST_OPENED) {
                sortLastOpened(books, booksMetadata)
            }
            else if(sortMethod == BooksSortMethod.SORT_OPENED_COUNT) {
                sortOpenedCount(books, booksMetadata)
            }
            else if(sortMethod == BooksSortMethod.SORT_RANDOM) {
                sortRandom(books)
            }
            else {
                books.sortWith { a: Book, b: Book ->
                    return@sortWith when(sortMethod) {
                        BooksSortMethod.SORT_ALPHABETIC -> a.normalisedTitle.compareTo(b.normalisedTitle)
                        BooksSortMethod.SORT_AGE -> a.publishedOn.compareTo(b.publishedOn)
                        BooksSortMethod.SORT_LENGTH -> a.pages.compareTo(b.pages)
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
}

enum class BooksSortMethod {
    SORT_ALPHABETIC, SORT_AGE, SORT_LENGTH, SORT_LAST_OPENED, SORT_OPENED_COUNT, SORT_RANDOM
}