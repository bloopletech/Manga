package net.bloople.manga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.regex.Pattern

internal class BooksSearcher {
    var searchText = ""
    var filterIds: ArrayList<Long> = ArrayList()

    suspend fun search(library: Library): ArrayList<Book> {
        val books = ArrayList<Book>()

        withContext(Dispatchers.Default) {
            val searchTerms = parseSearchTerms()

            bookLoop@ for((key, b) in library.books) {
                if(filterIds.isNotEmpty() && !filterIds.contains(key)) continue@bookLoop

                val compareTitle = b.title.lowercase(Locale.getDefault())
                for(searchTerm in searchTerms) {
                    if(searchTerm.startsWith("-")) {
                        val realSearchTerm = searchTerm.substring(1)
                        if(realSearchTerm == SPECIAL_LONG_BOOK) {
                            if(b.pages >= LONG_BOOK_PAGES) continue@bookLoop
                        }
                        else if(compareTitle.contains(realSearchTerm)) continue@bookLoop
                    }
                    else {
                        if(searchTerm == SPECIAL_LONG_BOOK) {
                            if(b.pages < LONG_BOOK_PAGES) continue@bookLoop
                        }
                        else if(!compareTitle.contains(searchTerm)) continue@bookLoop
                    }
                }
                books.add(b)
            }
        }

        return books
    }

    private fun parseSearchTerms(): ArrayList<String> {
        val terms = ArrayList<String>()
        val searchPattern = Pattern.compile("\"[^\"]*\"|[^ ]+")
        val matcher = searchPattern.matcher(searchText.toLowerCase())
        while(matcher.find()) terms.add(matcher.group().replace("\"", ""))
        return terms
    }

    companion object {
        const val LONG_BOOK_PAGES = 100
        const val SPECIAL_LONG_BOOK = "s.long"
    }
}