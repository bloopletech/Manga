package net.bloople.manga;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.IOException;

public class LibraryService {
    private static final int LIBRARY_CACHE_MAX_COUNT = 5;
    private static LruCache<Long, Library> currentLibraries = new LruCache<>(LIBRARY_CACHE_MAX_COUNT);

    private Context context;
    private Library library;
    private LibraryLoadedListener listener;
    private ProgressDialog loadingLibraryDialog;

    public static void ensureLibrary(Context context, long libraryId, final LibraryLoadedListener listener) {
        Library library = Library.findById(context, libraryId);
        if(library == null) library = Library.findDefault(context);

        if(library == null) {
            listener.onLibraryLoaded(null);
            return;
        }

        Library current = currentLibraries.get(library.id());

        if(current != null && current.root().equals(library.root())) {
            listener.onLibraryLoaded(current);
            return;
        }

        LibraryService service = new LibraryService(context, library, libraryResult -> {
            if(libraryResult != null) currentLibraries.put(libraryResult.id(), libraryResult);
            listener.onLibraryLoaded(libraryResult);
        });
        service.ensureLibrary();
    }

    public interface LibraryLoadedListener {
        void onLibraryLoaded(Library library);
    }

    private LibraryService(Context context, Library library, LibraryLoadedListener listener) {
        this.context = context;
        this.library = library;
        this.listener = listener;
    }

    private void showLoadingLibraryDialog() {
        if(loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
        loadingLibraryDialog = ProgressDialog.show(
                context,
                "Loading " + library.name(),
                "Please wait while the library is loaded...",
                true);
    }

    private void ensureLibrary() {
        showLoadingLibraryDialog();
        LoadLibraryTask loader = new LoadLibraryTask();
        loader.execute(library);
    }

    class LoadLibraryTask extends AsyncTask<Library, Void, Library> {
        protected Library doInBackground(Library... libraries) {
            Library library = libraries[0];

            try {
                library.inflate();
            }
            catch(IOException e) {
                e.printStackTrace();
                return null;
            }

            return library;
        }

        protected void onPostExecute(Library library) {
            if(loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
            listener.onLibraryLoaded(library);
        }
    }
}
