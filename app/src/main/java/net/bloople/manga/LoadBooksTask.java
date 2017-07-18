package net.bloople.manga;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by i on 19/07/2017.
 */

class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {
    private Context context;
    private BooksLoadedListener listener;

    LoadBooksTask(Context context, BooksLoadedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    protected List<Book> doInBackground(Void... params) {
        List<Book> books;

        try {
            books = new BooksLoader(context).load();
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

    protected void onPostExecute(List<Book> books) {
        MangaApplication.allBooks = new HashMap<>();
        for(Book b : books) MangaApplication.allBooks.put(b.id(), b);

        listener.onBooksLoaded();
    }
}