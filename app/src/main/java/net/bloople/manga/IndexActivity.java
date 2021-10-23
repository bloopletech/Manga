package net.bloople.manga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import net.bloople.manga.audit.LibrariesAuditor;

public class IndexActivity extends AppCompatActivity implements LibrariesFragment.OnLibrarySelectedListener {
    private IndexViewModel model;
    private LibrariesFragment librariesFragment;
    private LibrariesAuditor auditor;
    private BooksAdapter adapter;
    private AutoCompleteTextView searchField;
    private TextView searchResultsToolbar;

    private QueryService queryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);

        model = new ViewModelProvider(this).get(IndexViewModel.class);

        librariesFragment = (LibrariesFragment) getSupportFragmentManager().findFragmentById(R.id.libraries_fragment);

        auditor = new LibrariesAuditor(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResultsToolbar = findViewById(R.id.search_results_toolbar);

        model.getSorterDescription().observe(this, description -> {
            searchResultsToolbar.setText(description);
        });

        searchField = findViewById(R.id.search_field);

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if(actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                searchField.clearFocus();

                onSearch(searchField.getText().toString());

                handled = true;
            }
            return handled;
        });

        searchField.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                int clickIndex = searchField.getRight() -
                        searchField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();

                if(event.getRawX() >= clickIndex) {
                    searchField.setText("");
                    onSearch("");

                    return true;
                }
            }
            return false;
        });

        RecyclerView booksView = findViewById(R.id.books_view);
        GridLayoutManager booksLayoutManager = new GridLayoutManager(this, 4);
        booksView.setLayoutManager(booksLayoutManager);

        RequestManager requestManager = Glide.with(this);
        ViewPreloadSizeProvider<GlideUrl> sizeProvider = new ViewPreloadSizeProvider<>();

        adapter = new BooksAdapter(requestManager, sizeProvider);
        booksView.setAdapter(adapter);

        RecyclerViewPreloader<GlideUrl> preloader = new RecyclerViewPreloader<>(
            requestManager, adapter, sizeProvider, 12);

        booksView.addOnScrollListener(preloader);

        CollectionsManager collections = new CollectionsManager(this, adapter);
        collections.setup();

        queryService = new QueryService(this, searchField);

        model.getSearchResults().observe(this, searchResults -> {
            adapter.update(searchResults);
        });

        final Intent intent = getIntent();
        long intentLibraryId = intent.getLongExtra("libraryId", -1);

        if(intentLibraryId != -1) {
            loadLibrary(intentLibraryId);
        }
        else if(savedInstanceState == null) loadLibrary(-1L);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        model.setSort(savedInstanceState.getInt("sortMethod"), savedInstanceState.getBoolean("sortDirectionAsc"));
        loadLibrary(savedInstanceState.getLong("libraryId"));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Library library = model.getLibrary();
        if(library !=  null) savedInstanceState.putLong("libraryId", library.getId());
        savedInstanceState.putInt("sortMethod", model.getSortMethod());
        savedInstanceState.putBoolean("sortDirectionAsc", model.getSortDirectionAsc());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> IndexActivity.this.finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int sortMethod = model.getSortMethod();
        int newSortMethod = sortMethod;

        if(menuItem.getItemId() == R.id.sort_alphabetic) {
            newSortMethod = BooksSorter.SORT_ALPHABETIC;
        }
        else if(menuItem.getItemId() == R.id.sort_age) {
            newSortMethod = BooksSorter.SORT_AGE;
        }
        else if(menuItem.getItemId() == R.id.sort_size) {
            newSortMethod = BooksSorter.SORT_LENGTH;
        }
        else if(menuItem.getItemId() == R.id.sort_last_opened) {
            newSortMethod = BooksSorter.SORT_LAST_OPENED;
        }
        else if(menuItem.getItemId() == R.id.sort_opened_count) {
            newSortMethod = BooksSorter.SORT_OPENED_COUNT;
        }
        else if(menuItem.getItemId() == R.id.sort_random) {
            newSortMethod = BooksSorter.SORT_RANDOM;
        }

        boolean sortDirectionAsc = model.getSortDirectionAsc();
        if(sortMethod == newSortMethod) sortDirectionAsc = !sortDirectionAsc;
        model.setSort(newSortMethod, sortDirectionAsc);

        return true;
    }

    private void loadLibrary(long libraryId) {
        LibraryService.ensureLibrary(this, libraryId, library -> {
            if(library == null) return;
            librariesFragment.setCurrentLibraryId(library.getId());
            auditor.selected(library);
            model.setLibrary(library);
        });
    }

    private void onSearch(String text) {
        model.setSearchText(text);
        queryService.onSearch(text);
    }

    public void useList(BookList list) {
        model.useList(list);
    }

    public void onLibrarySelected(long libraryId) {
        loadLibrary(libraryId);
    }

    public void useTag(String tag) {
        String text = "\"" + tag + "\"";
        searchField.setText(text);
        onSearch(text);
    }
}
