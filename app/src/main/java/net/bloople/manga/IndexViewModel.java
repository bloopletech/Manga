package net.bloople.manga;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IndexViewModel extends AndroidViewModel {
    private Application application;
    private Library library;
    private MutableLiveData<ArrayList<Book>> searchResults = new MutableLiveData<>();
    private BooksSearcher searcher = new BooksSearcher();
    private BooksSorter sorter = new BooksSorter();
    private MutableLiveData<String> sorterDescription;

    public IndexViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setLibrary(Library library) {
        this.library = library;
        resolve();
    }

    public Library getLibrary() {
        return library;
    }

    public LiveData<ArrayList<Book>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getSorterDescription() {
        if(sorterDescription == null) {
            sorterDescription = new MutableLiveData<>(sorter.description());
        }
        return sorterDescription;
    }

    public int getSortMethod() {
        return sorter.getSortMethod();
    }

    public boolean getSortDirectionAsc() {
        return sorter.getSortDirectionAsc();
    }

    public void setSearchText(String searchText) {
        searcher.setSearchText(searchText);
        resolve();
    }

    public void setSort(int sortMethod, boolean sortDirectionAsc) {
        sorter.setSortMethod(sortMethod);
        sorter.setSortDirectionAsc(sortDirectionAsc);
        sorterDescription.setValue(sorter.description());
        resolve();
    }

    public void useList(BookList list) {
        if(list == null) searcher.setFilterIds(null);
        else searcher.setFilterIds(list.bookIds(application));
        resolve();
    }

    private void resolve() {
        ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(() -> {
            ArrayList<Book> books = searcher.search(library);
            sorter.sort(application, books);
            searchResults.postValue(books);
        });
    }
}
