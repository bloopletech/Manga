package net.bloople.manga;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import java.io.IOException;


class LoadLibraryTask extends AsyncTask<Uri, Void, LibraryLoader> {
    private LibraryLoadedListener listener;

    LoadLibraryTask(LibraryLoadedListener listener) {
        this.listener = listener;
    }

    protected LibraryLoader doInBackground(Uri... urls) {
        LibraryLoader loader = new LibraryLoader(urls[0]);

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

    protected void onPostExecute(LibraryLoader loader) {
        Library.current = new Library(loader.root(), loader.books(), loader.tags());

        listener.onLibraryLoaded();
    }
}