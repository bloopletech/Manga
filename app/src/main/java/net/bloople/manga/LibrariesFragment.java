package net.bloople.manga;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LibrariesFragment extends Fragment implements LibraryEditFragment.OnLibraryEditFinishedListener {
    private Context context;
    private OnLibrarySelectedListener listener;
    private LibrariesAdapter librariesAdapter;

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

        RecyclerView librariesView = view.findViewById(R.id.libraries);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        librariesView.setLayoutManager(layoutManager);

        librariesAdapter = new LibrariesAdapter(this);
        librariesView.setAdapter(librariesAdapter);

        updateCursor();
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

    void setCurrentLibraryId(long libraryId) {
        librariesAdapter.setCurrentLibraryId(libraryId);
    }

    void show(long libraryId) {
        listener.onLibrarySelected(libraryId);
    }

    void newLibrary() {
        Library library = new Library();
        library.name("New Library");
        library.root("http://example.com/");
        library.save(context);

        updateCursor();
    }

    void edit(long libraryId) {
        LibraryEditFragment childFragment = LibraryEditFragment.newInstance(libraryId);
        childFragment.show(getChildFragmentManager(), null);
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        Cursor result = db.rawQuery("SELECT * FROM library_roots", new String[] {});
        librariesAdapter.changeCursor(result);
    }
}
