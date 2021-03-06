package net.bloople.manga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import net.bloople.manga.audit.LibrariesAuditor;

import java.util.ArrayList;

public class IndexActivity extends AppCompatActivity implements LibrariesFragment.OnLibrarySelectedListener {
    private long libraryId = -1;
    private Library library;
    private LibrariesFragment librariesFragment;
    private LibrariesAuditor auditor;
    private RecyclerView booksView;
    private GridLayoutManager booksLayoutManager;
    private BooksAdapter adapter;
    private AutoCompleteTextView searchField;
    private TextView searchResultsToolbar;

    private BooksSearcher searcher = new BooksSearcher();
    private BooksSorter sorter = new BooksSorter();
    private QueryService queryService;
    private int pendingFirstItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);

        librariesFragment = (LibrariesFragment) getSupportFragmentManager().findFragmentById(R.id.libraries_fragment);

        auditor = new LibrariesAuditor(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResultsToolbar = findViewById(R.id.search_results_toolbar);

        searchField = findViewById(R.id.search_field);

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if(actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                searchField.clearFocus();

                resolve();

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
                    resolve();

                    return true;
                }
            }
            return false;
        });

        booksView = findViewById(R.id.books_view);
        booksLayoutManager = new GridLayoutManager(this, 4);
        booksView.setLayoutManager(booksLayoutManager);

        adapter = new BooksAdapter();
        booksView.setAdapter(adapter);

        CollectionsManager collections = new CollectionsManager(this, adapter);
        collections.setup();

        queryService = new QueryService(this, searchField);

        final Intent intent = getIntent();
        long intentLibraryId = intent.getLongExtra("libraryId", -1);

        if(intentLibraryId != -1) {
            libraryId = intentLibraryId;
            loadLibrary();
        }
        else if(savedInstanceState == null) loadLibrary();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        libraryId = savedInstanceState.getLong("libraryId");
        sorter.setSortMethod(savedInstanceState.getInt("sortMethod"));
        sorter.setSortDirectionAsc(savedInstanceState.getBoolean("sortDirectionAsc"));
        pendingFirstItemPosition = savedInstanceState.getInt("firstItemPosition");
        loadLibrary();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("libraryId", libraryId);
        savedInstanceState.putInt("sortMethod", sorter.getSortMethod());
        savedInstanceState.putBoolean("sortDirectionAsc", sorter.getSortDirectionAsc());
        int firstItemPosition = booksLayoutManager.findFirstCompletelyVisibleItemPosition();
        savedInstanceState.putInt("firstItemPosition", firstItemPosition);
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
        if(menuItem.getItemId() == R.id.sort_alphabetic) {
            if(sorter.getSortMethod() == BooksSorter.SORT_ALPHABETIC) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_ALPHABETIC);
        }
        else if(menuItem.getItemId() == R.id.sort_age) {
            if(sorter.getSortMethod() == BooksSorter.SORT_AGE) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_AGE);
        }
        else if(menuItem.getItemId() == R.id.sort_size) {
            if(sorter.getSortMethod() == BooksSorter.SORT_LENGTH) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_LENGTH);
        }
        else if(menuItem.getItemId() == R.id.sort_last_opened) {
            if(sorter.getSortMethod() == BooksSorter.SORT_LAST_OPENED) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_LAST_OPENED);
        }
        else if(menuItem.getItemId() == R.id.sort_opened_count) {
            if(sorter.getSortMethod() == BooksSorter.SORT_OPENED_COUNT) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_OPENED_COUNT);
        }
        else if(menuItem.getItemId() == R.id.sort_random) {
            if(sorter.getSortMethod() == BooksSorter.SORT_RANDOM) sorter.flipSortDirection();
            sorter.setSortMethod(BooksSorter.SORT_RANDOM);
        }

        resolve();

        return true;
    }

    private void loadLibrary() {
        LibraryService.ensureLibrary(this, libraryId, library -> {
            if(library == null) return;
            IndexActivity.this.library = library;
            librariesFragment.setCurrentLibraryId(library.id());
            auditor.selected(library);
            resolve();
        });
    }

    private void resolve() {
        String text = searchField.getText().toString();
        searcher.setSearchText(text);
        queryService.onSearch(text);
        searchResultsToolbar.setText(sorter.description());
        ResolverTask resolver = new ResolverTask();
        resolver.execute();
    }

    public void useList(BookList list) {
        if(list == null) searcher.setFilterIds(null);
        else searcher.setFilterIds(list.bookIds(this));
        resolve();
    }

    public void onLibrarySelected(long libraryId) {
        this.libraryId = libraryId;
        loadLibrary();
    }

    public void useTag(String tag) {
        searchField.setText("\"" + tag + "\"");
        resolve();
    }

    class ResolverTask extends AsyncTask<Void, Void, ArrayList<Long>> {
        @Override
        protected ArrayList<Long> doInBackground(Void... voids) {
            ArrayList<Book> books = searcher.search(library);
            sorter.sort(IndexActivity.this, books);

            ArrayList<Long> bookIds = new ArrayList<>();
            for(Book b : books) bookIds.add(b.id());
            return bookIds;
        }

        @Override
        protected void onPostExecute(ArrayList<Long> bookIds) {
            adapter.update(library, bookIds);
            booksView.scrollToPosition(pendingFirstItemPosition);
            pendingFirstItemPosition = 0;
        }
    }
}
