package net.bloople.manga;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

class LibrariesAdapter extends RecyclerView.Adapter<LibrariesAdapter.ViewHolder> {
    static final int VIEW_TYPE_CELL = 0;
    static final int VIEW_TYPE_FOOTER = 1;

    private LibrariesFragment fragment;
    private Cursor cursor;
    private long selectedLibraryId;

    LibrariesAdapter(LibrariesFragment fragment) {
        this.fragment = fragment;
    }

    void changeCursor(Cursor cursor) {
        this.cursor = cursor;
        System.out.println("cursor changed");
        notifyDataSetChanged();
    }

    void setCurrentLibraryId(long libraryId) {
        selectedLibraryId = libraryId;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(cursor == null) return 1;
        return cursor.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == cursor.getCount()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    class CellViewHolder extends ViewHolder {
        long libraryId;
        TextView nameView;
        TextView currentNameView;
        CellViewHolder(View view) {
            super(view);

            nameView = view.findViewById(R.id.name);
            currentNameView = view.findViewById(R.id.current_name);

            view.setOnClickListener(v -> {
                fragment.show(libraryId);
            });

            view.setOnLongClickListener(v -> {
                fragment.edit(libraryId);

                return true;
            });
        }
    }

    class FooterViewHolder extends ViewHolder {
        ImageButton newLibraryView;
        FooterViewHolder(View view) {
            super(view);

            newLibraryView = view.findViewById(R.id.new_library);
            newLibraryView.setOnClickListener(v -> fragment.newLibrary());
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LibrariesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_CELL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library, parent, false);
            return new LibrariesAdapter.CellViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_new_view, parent, false);
            return new LibrariesAdapter.FooterViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull LibrariesAdapter.ViewHolder holder, int position) {
        if(holder instanceof CellViewHolder) {
            cursor.moveToPosition(position);

            CellViewHolder cellHolder = (CellViewHolder)holder;

            long libraryId = cursor.getLong(cursor.getColumnIndex("_id"));
            cellHolder.libraryId = libraryId;

            final String name = cursor.getString(cursor.getColumnIndex("name"));

            if(libraryId == selectedLibraryId) {
                System.out.println("this library is selected");
                cellHolder.currentNameView.setText(name);
                cellHolder.nameView.setVisibility(View.GONE);
                cellHolder.currentNameView.setVisibility(View.VISIBLE);
            }
            else {
                cellHolder.nameView.setText(name);
                cellHolder.currentNameView.setVisibility(View.GONE);
                cellHolder.nameView.setVisibility(View.VISIBLE);
            }
        }
    }
}
