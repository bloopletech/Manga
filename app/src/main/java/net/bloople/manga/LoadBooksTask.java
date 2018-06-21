package net.bloople.manga;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


class LoadBooksTask extends AsyncTask<Void, Void, BooksLoader> {
    private BooksLoadedListener listener;

    LoadBooksTask(BooksLoadedListener listener) {
        this.listener = listener;
    }

    protected BooksLoader doInBackground(Void... params) {
        BooksLoader loader = new BooksLoader();

        try {
            loader.load();
        }
        catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        return loader;
    }

    protected void onPostExecute(BooksLoader loader) {
        MangaApplication.allBooks = loader.books();
        MangaApplication.allTags = loader.tags();

        listener.onBooksLoaded();
    }
}