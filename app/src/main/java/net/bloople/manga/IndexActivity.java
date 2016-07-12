package net.bloople.manga;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.json.JSONException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexActivity extends AppCompatActivity {
    private RecyclerView booksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        booksView = (RecyclerView)findViewById(R.id.books_view);
        booksView.setLayoutManager(new GridLayoutManager(this, 5));

        if(MangaApplication.books != null) {
            initAdapter();
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

    private void initAdapter() {
        final BooksAdapter adapter = new BooksAdapter(MangaApplication.books);

        adapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(IndexActivity.this, ReadingActivity.class);
                intent.putExtra("key", position);

                startActivity(intent);
            }
        });

        booksView.setAdapter(adapter);
    }

    private class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {

        protected List<Book> doInBackground(Void... params) {
            List<Book> books;

            try {
                books = new BooksLoader().load();
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

        protected void onPostExecute(List<Book> books) {
            MangaApplication.books = books;
            initAdapter();
        }
    }
}
