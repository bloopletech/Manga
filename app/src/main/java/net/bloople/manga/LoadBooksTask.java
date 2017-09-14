package net.bloople.manga;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


class LoadBooksTask extends AsyncTask<Void, Void, List<Book>> {
    private BooksLoadedListener listener;

    LoadBooksTask(BooksLoadedListener listener) {
        this.listener = listener;
    }

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

        return books;
    }

    protected void onPostExecute(List<Book> books) {
        MangaApplication.allBooks = new HashMap<>();
        for(Book b : books) MangaApplication.allBooks.put(b.id(), b);

        listener.onBooksLoaded();
    }
}