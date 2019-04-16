package net.bloople.manga;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

class LibraryService {
    private static Library current;

    private Context context;
    private LibraryRoot libraryRoot;
    private LibraryLoadedListener listener;
    private ProgressDialog loadingLibraryDialog;

    static void ensureLibrary(Context context, long libraryRootId, final LibraryLoadedListener listener) {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        if(libraryRoot == null) libraryRoot = LibraryRoot.findDefault(context);

        if(current != null && current.root().equals(libraryRoot.rootUri())) {
            listener.onLibraryLoaded(current);
        }
        else {
            LibraryService service = new LibraryService(context, libraryRoot, library -> {
                current = library;
                listener.onLibraryLoaded(library);
            });
            service.ensureLibrary();
        }
    }

    interface LibraryLoadedListener {
        void onLibraryLoaded(Library library);
    }

    private LibraryService(Context context, LibraryRoot libraryRoot, LibraryLoadedListener listener) {
        this.context = context;
        this.libraryRoot = libraryRoot;
        this.listener = listener;
    }

    private void showLoadingLibraryDialog() {
        if(loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
        loadingLibraryDialog = ProgressDialog.show(
                context,
                "Loading " + libraryRoot.name(),
                "Please wait while the library is loaded...",
                true);
    }

    private void ensureLibrary() {
        showLoadingLibraryDialog();
        LoadLibraryTask loader = new LoadLibraryTask();
        loader.execute(libraryRoot);
    }

    class LoadLibraryTask extends AsyncTask<LibraryRoot, Void, LibraryLoader> {
        protected LibraryLoader doInBackground(LibraryRoot... libraryRoots) {
            LibraryLoader loader = new LibraryLoader(libraryRoots[0]);

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
            if(loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
            listener.onLibraryLoaded(loader == null ? null : loader.library());
        }
    }
}
