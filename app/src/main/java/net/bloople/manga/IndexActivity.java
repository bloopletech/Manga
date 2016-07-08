package net.bloople.manga;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        LoadBooksTask loader = new LoadBooksTask();
        loader.execute();
    }

    private class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {
        protected List<Book> doInBackground(Void... params) {
            try {
                URL root = new URL("http://192.168.1.2/Manga-OG/.mangos/");
                BooksLoader loader = new BooksLoader(root);
                return loader.load();
            }
            catch(IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(List<Book> books) {
            MangaApplication.books = books;
        }
    }
}
