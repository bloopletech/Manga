package net.bloople.manga

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class IndexViewModel(application: Application) : AndroidViewModel(application) {
    private var library = Library.EMPTY
    private val searcher = BooksSearcher()
    private val sorter = BooksSorter()

    val searchResults: MutableLiveData<SearchResults> by lazy {
        MutableLiveData<SearchResults>()
    }

    val searchResultsDescription: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setLibrary(library: Library) {
        this.library = library
        resolve()
    }

    fun getLibrary(): Library {
        return library
    }

    val sortMethod: BooksSortMethod
        get() = sorter.sortMethod
    val sortDirectionAsc: Boolean
        get() = sorter.sortDirectionAsc

    fun setSearchText(searchText: String) {
        searcher.searchText = searchText
        resolve()
    }

    fun setSort(sortMethod: BooksSortMethod, sortDirectionAsc: Boolean) {
        sorter.sortMethod = sortMethod
        sorter.sortDirectionAsc = sortDirectionAsc
        resolve()
    }

    private fun resolve() {
        viewModelScope.launch {
            val books = searcher.search(library)
            val booksMetadata = BookMetadata.findAllByBookIds(books)
            sorter.sort(books, booksMetadata)
            searchResultsDescription.postValue(buildSearchResultsDescription(books.size))
            searchResults.postValue(SearchResults(books, booksMetadata))
        }
    }

    private fun buildSearchResultsDescription(resultsCount: Int): String {
        if(searcher.isPresent()) {
            return "${resultsCount.f} of ${library.books.size.f("books")} sorted by ${sorter.sortMethodDescription.l} ${sorter.sortDirectionDescription.l}"
        }
        return "${library.books.size.f("books")} sorted by ${sorter.sortMethodDescription.l} ${sorter.sortDirectionDescription.l}"
    }
}