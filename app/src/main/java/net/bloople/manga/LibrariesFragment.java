package net.bloople.manga;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LibrariesFragment extends Fragment implements LibraryEditFragment.OnLibraryEditFinishedListener {
    private Context context;
    private OnLibrarySelectedListener listener;
    private SimpleCursorAdapter librariesAdapter;

    interface OnLibrarySelectedListener {
        void onLibrarySelected(long libraryId);
    }

     @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listener = (OnLibrarySelectedListener)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.libraries_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        librariesAdapter = new SimpleCursorAdapter(context,
                R.layout.library,
                null,
                new String[] { "name" },
                new int[] { R.id.name },
                0);

        ListView librariesView = view.findViewById(R.id.libraries);
        librariesView.setAdapter(librariesAdapter);
        updateCursor();

        librariesView.setOnItemClickListener((parent, v, position, id) -> {
            long libraryId = parent.getItemIdAtPosition(position);
            listener.onLibrarySelected(libraryId);
        });

        librariesView.setOnItemLongClickListener((parent, v, position, id) -> {
            long libraryId = parent.getItemIdAtPosition(position);
            edit(libraryId);

            return true;
        });

        ImageButton newLibrary = view.findViewById(R.id.new_library);
        newLibrary.setOnClickListener(v -> newLibrary());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        this.listener = null;
    }

    @Override
    public void onLibraryEditFinished(Library library) {
        updateCursor();
    }

    private void newLibrary() {
        Library library = new Library();
        library.name("New Library");
        library.root("http://example.com/");
        library.save(context);

        updateCursor();
    }

    private void edit(long libraryId) {
        LibraryEditFragment childFragment = LibraryEditFragment.newInstance(libraryId);
        childFragment.show(getChildFragmentManager(), null);
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        Cursor result = db.rawQuery("SELECT * FROM library_roots", new String[] {});
        librariesAdapter.changeCursor(result);
    }
}
