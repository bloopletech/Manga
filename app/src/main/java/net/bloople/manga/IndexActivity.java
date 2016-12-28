package net.bloople.manga;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.bignerdranch.android.multiselector.MultiSelector;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static android.R.id.list;
import static net.bloople.manga.BookSearcher.SORT_AGE;
import static net.bloople.manga.BookSearcher.SORT_ALPHABETIC;
import static net.bloople.manga.BookSearcher.SORT_LAST_OPENED;
import static net.bloople.manga.BookSearcher.SORT_LENGTH;

public class IndexActivity extends Activity {
    private RecyclerView booksView;
    private BooksAdapter adapter;
    private MultiSelector multiSelector;

    private BookSearcher searcher = new BookSearcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/storage/emulated/0/Download"));
        }

        setContentView(R.layout.activity_index);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setActionBar(toolbar);

        final EditText searchField = (EditText)findViewById(R.id.search_field);
        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    searchField.clearFocus();

                    searcher.setSearchText(v.getText().toString());
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
                        searcher.setSearchText("");
                        resolve();

                        return true;
                    }
                }
                return false;
            }
        });

        booksView = (RecyclerView)findViewById(R.id.books_view);
        booksView.setLayoutManager(new GridLayoutManager(this, 4));

        multiSelector = new MultiSelector();
        adapter = new BooksAdapter(multiSelector);
        booksView.setAdapter(adapter);

        ArrayList<BookList> lists = BookList.all(this);
        if(lists != null) {
            for(BookList list : lists) {
                System.out.println("list: " + list.name());
                ArrayList<String> keys = list.bookKeys(this);
                for(String key : keys) System.out.println(key);
                System.out.println("====================================================");
            }
        }

        if(MangaApplication.allBooks != null) {
            resolve();
        }
        else {
            LoadBooksTask loader = new LoadBooksTask();
            loader.execute();
        }
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
            if(searcher.getSortMethod() == BookSearcher.SORT_ALPHABETIC) searcher.flipSortDirection();
            searcher.setSortMethod(BookSearcher.SORT_ALPHABETIC);
        }
        else if(menuItem.getItemId() == R.id.sort_age) {
            if(searcher.getSortMethod() == BookSearcher.SORT_AGE) searcher.flipSortDirection();
            searcher.setSortMethod(BookSearcher.SORT_AGE);
        }
        else if(menuItem.getItemId() == R.id.sort_size) {
            if(searcher.getSortMethod() == BookSearcher.SORT_LENGTH) searcher.flipSortDirection();
            searcher.setSortMethod(BookSearcher.SORT_LENGTH);
        }
        else if(menuItem.getItemId() == R.id.sort_last_opened) {
            if(searcher.getSortMethod() == BookSearcher.SORT_LAST_OPENED) searcher.flipSortDirection();
            searcher.setSortMethod(BookSearcher.SORT_LAST_OPENED);
        }
        else if(menuItem.getItemId() == R.id.manage_indexing) {
            boolean wasOn = multiSelector.isSelectable();
            multiSelector.setSelectable(!multiSelector.isSelectable());

            if(wasOn) {
                List<Integer> positions = multiSelector.getSelectedPositions();

                String name = UUID.randomUUID().toString();


                ArrayList<String> keys = new ArrayList<>();

                for(int i : positions) keys.add(adapter.at(i).key());

                BookList list = new BookList();
                list.name(name);
                list.save(this);
                list.bookKeys(this, keys);

                multiSelector.clearSelections();
            }
            //Intent intent = new Intent(BooksActivity.this, IndexingActivity.class);
            //startActivity(intent);
        }

        resolve();

        return true;
    }

    private void resolve() {
        adapter.update(searcher.resolve());
        booksView.scrollToPosition(0);
    }

    private class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {

        protected List<Book> doInBackground(Void... params) {
            List<Book> books;

            try {
                books = new BooksLoader(IndexActivity.this).load();
            }
            catch(JSONException e) {
                e.printStackTrace();
                return null;
            }
            catch(IOException e) {
                e.printStackTrace();
                return null;
            }

            return books;
        }

        protected void onPostExecute(List<Book> allBooks) {
            MangaApplication.allBooks = allBooks;
            resolve();
        }
    }
}
