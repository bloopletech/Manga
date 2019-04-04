package net.bloople.manga;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LibraryRootsFragment extends Fragment implements LibraryRootEditFragment.OnLibraryRootEditFinishedListener {
    private Context context;
    private OnLibraryRootSelectedListener listener;
    private SimpleCursorAdapter libraryRootsAdapter;

    public interface OnLibraryRootSelectedListener {
        void onLibraryRootSelected(long libraryRootId);
    }

     @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listener = (OnLibraryRootSelectedListener)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_roots_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        libraryRootsAdapter = new SimpleCursorAdapter(context,
                R.layout.library_root,
                null,
                new String[] { "name" },
                new int[] { R.id.name },
                0);

        ListView libraryRootsView = view.findViewById(R.id.library_roots);
        libraryRootsView.setAdapter(libraryRootsAdapter);
        updateCursor();

        libraryRootsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long libraryRootId = parent.getItemIdAtPosition(position);
                listener.onLibraryRootSelected(libraryRootId);
            }
        });

        libraryRootsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long libraryRootId = parent.getItemIdAtPosition(position);
                edit(libraryRootId);

                return true;
            }
        });

        ImageButton newLibrary = view.findViewById(R.id.new_library);
        newLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLibrary();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        this.listener = null;
    }

    @Override
    public void onLibraryRootEditFinished(LibraryRoot libraryRoot) {
        updateCursor();
    }

    private void newLibrary() {
        LibraryRoot libraryRoot = new LibraryRoot();
        libraryRoot.name("New Library");
        libraryRoot.root("http://example.com/");
        libraryRoot.save(context);

        updateCursor();
    }

    private void edit(long libraryRootId) {
        LibraryRootEditFragment childFragment = LibraryRootEditFragment.newInstance(libraryRootId);
        childFragment.show(getChildFragmentManager(), null);
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        Cursor result = db.rawQuery("SELECT * FROM library_roots", new String[] {});
        libraryRootsAdapter.changeCursor(result);
    }
}
