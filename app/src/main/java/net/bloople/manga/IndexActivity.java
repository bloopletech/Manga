package net.bloople.manga;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toolbar;

import java.util.ArrayList;

public class IndexActivity extends Activity implements BooksLoadedListener {
    private RecyclerView booksView;
    private BooksAdapter adapter;
    private EditText searchField;

    private BooksSearcher searcher = new BooksSearcher();
    private BooksSorter sorter = new BooksSorter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/storage/emulated/0/Download"));
        }

        setContentView(R.layout.activity_index);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setActionBar(toolbar);

        searchField = (EditText)findViewById(R.id.search_field);

        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    searchField.clearFocus();

                    resolve();

                    handled = true;
                }
                return handled;
            }
        });

        searchField.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
            }
        });

        booksView = (RecyclerView)findViewById(R.id.books_view);
        booksView.setLayoutManager(new GridLayoutManager(this, 4));

        adapter = new BooksAdapter();
        booksView.setAdapter(adapter);

        CollectionsManager collections = new CollectionsManager(this, adapter);
        collections.setup();

        if(savedInstanceState == null) resolveOrLoad();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        resolveOrLoad();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        IndexActivity.this.finish();
                    }
                })
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

        resolve();

        return true;
    }

    public void onBooksLoaded() {
        resolve();
    }

    private void resolveOrLoad() {
        MangaApplication.ensureAllBooks(this);
    }

    private void resolve() {
        searcher.setSearchText(searchField.getText().toString());

        ArrayList<Book> books = searcher.search();
        sorter.sort(this, books);

        ArrayList<Long> bookIds = new ArrayList<>();
        for(Book b : books) bookIds.add(b.id());

        adapter.update(bookIds);
        booksView.scrollToPosition(0);
    }

    public void useList(BookList list) {
        if(list == null) searcher.setFilterIds(null);
        else searcher.setFilterIds(list.bookIds(this));
        resolve();
    }

}
