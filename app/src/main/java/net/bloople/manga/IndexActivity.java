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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexActivity extends Activity {
    private RecyclerView booksView;
    private BooksAdapter adapter;
    private BookSearcher searcher = new BookSearcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/storage/emulated/0/Download"));
        }

        setContentView(R.layout.activity_index);

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

        adapter = new BooksAdapter();
        adapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(IndexActivity.this, ReadingActivity.class);
                intent.putExtra("key", adapter.at(position).key());

                startActivity(intent);
            }
        });

        booksView.setAdapter(adapter);

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

    private void resolve() {
        adapter.update(searcher.resolve());
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

            Collections.sort(books, new Comparator<Book>() {
                public int compare(Book o1, Book o2) {
                    if(o1.publishedOn() == o2.publishedOn()) return 0;
                    return (o1.publishedOn() < o2.publishedOn()) ? 1 : -1;
                }
            });

            return books;
        }

        protected void onPostExecute(List<Book> allBooks) {
            MangaApplication.allBooks = allBooks;
            resolve();
        }
    }
}
