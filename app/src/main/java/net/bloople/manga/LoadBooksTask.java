package net.bloople.manga;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import java.io.IOException;


class LoadBooksTask extends AsyncTask<Uri, Void, BooksLoader> {
    private LibraryLoadedListener listener;

    LoadBooksTask(LibraryLoadedListener listener) {
        this.listener = listener;
    }

    protected BooksLoader doInBackground(Uri... urls) {
        BooksLoader loader = new BooksLoader(urls[0]);

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
        Library.current = new Library(loader.root(), loader.books(), loader.tags());

        listener.onLibraryLoaded();
    }
}