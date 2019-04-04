package net.bloople.manga;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

class LibraryService {
    static long currentLibraryRootId;
    static Library current;

    private Context context;
    private long libraryRootId;
    private ProgressDialog loadingLibraryDialog;

    public interface LibraryLoadedListener {
        void onLibraryLoaded();
    }

    public static void ensureLibrary(Context context, long libraryRootId) {
        currentLibraryRootId = libraryRootId;

        LibraryService service = new LibraryService(context, currentLibraryRootId);
        service.ensureLibrary();
    }

    private LibraryService(Context context, long libraryRootId) {
        this.context = context;
        this.libraryRootId = libraryRootId;
    }

    private void showLoadingLibraryDialog(LibraryRoot libraryRoot) {
        if (loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
        loadingLibraryDialog = ProgressDialog.show(
                context,
                "Loading " + libraryRoot.name(),
                "Please wait while the library is loaded...",
                true);
    }

    private void ensureLibrary() {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        if (libraryRoot == null) libraryRoot = LibraryRoot.findDefault(context);
        Uri rootUri = Uri.parse(libraryRoot.root());

        if (current != null && current.root().equals(rootUri)) {
            ((LibraryLoadedListener) context).onLibraryLoaded();
        } else {
            showLoadingLibraryDialog(libraryRoot);
            LoadLibraryTask loader = new LoadLibraryTask();
            loader.execute(rootUri);
        }
    }

    class LoadLibraryTask extends AsyncTask<Uri, Void, LibraryLoader> {
        protected LibraryLoader doInBackground(Uri... urls) {
            LibraryLoader loader = new LibraryLoader(urls[0]);

            try {
                loader.load();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return loader;
        }

        protected void onPostExecute(LibraryLoader loader) {
            current = loader.library();
            if (loadingLibraryDialog != null) loadingLibraryDialog.dismiss();
            ((LibraryLoadedListener) context).onLibraryLoaded();
        }
    }
}
