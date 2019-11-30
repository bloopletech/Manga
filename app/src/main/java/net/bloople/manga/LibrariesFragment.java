package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class LibrariesFragment extends Fragment implements LibraryEditFragment.OnLibraryEditFinishedListener {
    private Context context;
    private OnLibrarySelectedListener listener;
    private LibrariesAdapter librariesAdapter;

    private ImageButton startEditingButton;
    private ImageButton finishEditingButton;
    private ImageButton newLibraryButton;
    private ItemTouchHelper touchHelper;
    private boolean editingMode = false;

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

        librariesAdapter = new LibrariesAdapter(this, null);
        librariesView.setAdapter(librariesAdapter);

        startEditingButton = view.findViewById(R.id.start_editing);
        startEditingButton.setOnClickListener(v -> {
            startEditingButton.setVisibility(View.GONE);
            newLibraryButton.setVisibility(View.VISIBLE);
            finishEditingButton.setVisibility(View.VISIBLE);
            editingMode = true;
        });

        finishEditingButton = view.findViewById(R.id.finish_editing);
        finishEditingButton.setOnClickListener(v -> {
            editingMode = false;
            finishEditingButton.setVisibility(View.GONE);
            newLibraryButton.setVisibility(View.GONE);
            startEditingButton.setVisibility(View.VISIBLE);
        });

        newLibraryButton = view.findViewById(R.id.new_library);
        newLibraryButton.setOnClickListener(v -> create());

        touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                LibrariesAdapter.ViewHolder holderA = (LibrariesAdapter.ViewHolder)viewHolder;
                LibrariesAdapter.ViewHolder holderB = (LibrariesAdapter.ViewHolder)target;

                swap(holderA.libraryId, holderB.libraryId);
                librariesAdapter.notifyItemMoved(holderA.getAdapterPosition(), holderB.getAdapterPosition());

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // no-op
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }
        });
        touchHelper.attachToRecyclerView(librariesView);

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

    boolean isEditingMode() {
        return editingMode;
    }

    void startDrag(LibrariesAdapter.ViewHolder holder) {
        touchHelper.startDrag(holder);
    }

    void setCurrentLibraryId(long libraryId) {
        librariesAdapter.setCurrentLibraryId(libraryId);
    }

    void show(long libraryId) {
        listener.onLibrarySelected(libraryId);
    }

    void edit(long libraryId) {
        LibraryEditFragment childFragment = LibraryEditFragment.newInstance(libraryId);
        childFragment.show(getChildFragmentManager(), null);
    }

    private void create() {
        Library library = new Library();
        library.name("New Library");
        library.position(Library.findHighestPosition(context) + 1);
        library.root("http://example.com/");
        library.save(context);

        updateCursor();
    }

    void swap(long libraryAId, long libraryBId) {
        Library libraryA = Library.findById(context, libraryAId);
        Library libraryB = Library.findById(context, libraryBId);
        int aPosition = libraryA.position();
        libraryA.position(libraryB.position());
        libraryB.position(aPosition);
        libraryA.save(context);
        libraryB.save(context);

        updateCursor();
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        Cursor result = db.rawQuery("SELECT * FROM library_roots ORDER BY position ASC", new String[] {});
        result.moveToFirst();
        librariesAdapter.swapCursor(result);
    }
}
