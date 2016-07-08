package net.bloople.manga;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class IndexActivity extends AppCompatActivity {
    private RecyclerView booksView;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        booksView = (RecyclerView)findViewById(R.id.books_view);

        layoutManager = new GridLayoutManager(this, 3);
        booksView.setLayoutManager(layoutManager);

        LoadBooksTask loader = new LoadBooksTask();
        loader.execute();
    }

    private class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {

        protected List<Book> doInBackground(Void... params) {
            try {
                return new BooksLoader().load();
            }
            catch(JSONException e) {
                e.printStackTrace();
                return null;
            }
            catch(IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(List<Book> books) {
            MangaApplication.books = books;

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
    }
}
