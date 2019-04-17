package net.bloople.manga;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

class LibraryService {
    private static Library current;

    private Context context;
    private Library library;
    private LibraryLoadedListener listener;
    private ProgressDialog loadingLibraryDialog;

    static void ensureLibrary(Context context, long libraryId, final LibraryLoadedListener listener) {
        Library library = Library.findById(context, libraryId);
        if(library == null) library = Library.findDefault(context);

        if(current != null && current.rootUri().equals(library.rootUri())) {
            listener.onLibraryLoaded(current);
        }
        else {
            LibraryService service = new LibraryService(context, library, libraryResult -> {
                current = libraryResult;
                listener.onLibraryLoaded(libraryResult);
            });
            service.ensureLibrary();
        }
    }

    interface LibraryLoadedListener {
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
