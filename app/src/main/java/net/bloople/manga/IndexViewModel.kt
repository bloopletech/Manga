package net.bloople.manga

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

class IndexViewModel(application: Application) : AndroidViewModel(application) {
    private var library: Library? = null
    private val searcher = BooksSearcher()
    private val sorter = BooksSorter()

    val searchResults: MutableLiveData<SearchResults> by lazy {
        MutableLiveData<SearchResults>()
    }

    val sorterDescription: MutableLiveData<String> by lazy {
        MutableLiveData<String>(sorter.description())
    }

    fun setLibrary(library: Library) {
        this.library = library
        resolve()
    }

    fun getLibrary(): Library? {
        return library
    }

    val sortMethod: Int
        get() = sorter.sortMethod
    val sortDirectionAsc: Boolean
        get() = sorter.sortDirectionAsc

    fun setSearchText(searchText: String) {
        searcher.searchText = searchText
        resolve()
    }

    fun setSort(sortMethod: Int, sortDirectionAsc: Boolean) {
        sorter.sortMethod = sortMethod
        sorter.sortDirectionAsc = sortDirectionAsc
        sorterDescription.value = sorter.description()
        resolve()
    }

    fun useList(list: BookList?) {
        searcher.filterIds = list?.bookIds(getApplication()) ?: ArrayList()
        resolve()
    }

    private fun resolve() {
        val service = Executors.newSingleThreadExecutor()
        service.submit {
            val books = searcher.search(library ?: return@submit)
            val booksMetadata = BookMetadata.findAllByBookIds(getApplication(), books)
            sorter.sort(books, booksMetadata)
            searchResults.postValue(SearchResults(books, booksMetadata))
        }
    }
}