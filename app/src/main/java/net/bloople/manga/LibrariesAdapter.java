package net.bloople.manga;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class LibrariesAdapter extends CursorRecyclerAdapter<LibrariesAdapter.ViewHolder> {
    private LibrariesFragment fragment;
    private long selectedLibraryId;

    LibrariesAdapter(LibrariesFragment fragment, Cursor cursor) {
        super(cursor);
        this.fragment = fragment;
    }

    void setCurrentLibraryId(long libraryId) {
        selectedLibraryId = libraryId;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        long libraryId;
        TextView nameView;
        TextView currentNameView;
        ViewHolder(View view) {
            super(view);

            nameView = view.findViewById(R.id.name);
            currentNameView = view.findViewById(R.id.current_name);

            view.setOnClickListener(v -> {
                if(fragment.isEditingMode()) fragment.edit(libraryId);
                else fragment.show(libraryId);
            });

            view.setOnLongClickListener(v -> !fragment.isEditingMode());
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LibrariesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library, parent, false);
        return new LibrariesAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull LibrariesAdapter.ViewHolder holder, Cursor cursor) {
        Library library = new Library(cursor);

        holder.libraryId = library.id();

        if(library.id() == selectedLibraryId) {
            holder.currentNameView.setText(library.name());
            holder.nameView.setVisibility(View.GONE);
            holder.currentNameView.setVisibility(View.VISIBLE);
        }
        else {
            holder.nameView.setText(library.name());
            holder.currentNameView.setVisibility(View.GONE);
            holder.nameView.setVisibility(View.VISIBLE);
        }
    }
}
