package net.bloople.manga;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private long libraryRootId;
    private Library library;
    private ArrayList<Long> bookIds = new ArrayList<>();
    private ArrayList<Long> selectedBookIds = new ArrayList<>();
    private boolean selectable = false;

    BooksAdapter() {
        setHasStableIds(true);
    }

    public long getItemId(int position) {
        return bookIds.get(position);
    }

    public boolean isSelectable() {
        return selectable;
    }

    void setSelectable(boolean isSelectable) {
        selectable = isSelectable;
        notifyDataSetChanged();
    }

    ArrayList<Long> getSelectedBookIds() {
        return selectedBookIds;
    }

    void setSelectedBookIds(ArrayList<Long> selectedBookIds) {
        this.selectedBookIds = selectedBookIds;
        notifyDataSetChanged();
    }

    void clearSelectedBookIds() {
        setSelectedBookIds(new ArrayList<Long>());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookIds.size();
    }

    void update(long libraryRootId, Library library, ArrayList<Long> inBookIds) {
        this.libraryRootId = libraryRootId;
        this.library = library;
        bookIds = inBookIds;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView pageCountView;
        TextView textView;
        ImageView imageView;
        ImageView selectableView;
        ViewHolder(View view) {
            super(view);

            pageCountView = (TextView)view.findViewById(R.id.page_count_view);
            textView = (TextView)view.findViewById(R.id.text_view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
            selectableView = (ImageView)view.findViewById(R.id.selectable);

            view.setOnClickListener(v -> {
                long bookId = BooksAdapter.this.getItemId(getAdapterPosition());

                if(selectable) {
                    if(selectedBookIds.contains(bookId)) {
                        selectedBookIds.remove(bookId);
                        v.setActivated(false);
                    }
                    else {
                        selectedBookIds.add(bookId);
                        v.setActivated(true);
                    }

                    notifyItemChanged(getAdapterPosition());
                }
                else {
                    openBook(bookId, true);
                }
            });

            view.setOnLongClickListener(v -> {
                if(selectable) return false;

                long bookId = BooksAdapter.this.getItemId(getAdapterPosition());
                openBook(bookId, false);

                return true;
            });

            textView.setOnLongClickListener(v -> {
                if(selectable) return false;

                IndexActivity indexActivity = (IndexActivity)itemView.getContext();

                Book book = library.books().get(BooksAdapter.this.getItemId(getAdapterPosition()));
                TagChooserFragment tagChooser = TagChooserFragment.newInstance(book.tags().toArray(new String[0]));
                tagChooser.show(indexActivity.getFragmentManager(), "tag_chooser");

                return true;
            });
        }

        private void openBook(long bookId, boolean resume) {
            BookMetadata metadata = BookMetadata.findOrCreateByBookId(itemView.getContext(), bookId);
            metadata.lastOpenedAt(System.currentTimeMillis());
            metadata.save(itemView.getContext());

            Intent intent = new Intent(itemView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", resume);
            intent.putExtra("libraryRootId", libraryRootId);

            itemView.getContext().startActivity(intent);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_book_view, parent, false);

        final double viewWidthToBitmapWidthRatio = (double)parent.getWidth() / 4.0 / 197.0;
        view.getLayoutParams().height = (int)(310.0 * viewWidthToBitmapWidthRatio);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        long bookId = getItemId(position);
        Book book = library.books().get(bookId);

        holder.selectableView.setVisibility(selectable ? View.VISIBLE : View.INVISIBLE);
        if(selectable) holder.itemView.setActivated(selectedBookIds.contains(bookId));

        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.setText(book.title());

        holder.pageCountView.setText(String.format("%,d", book.pages()));

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Glide.with(holder.imageView.getContext()).load(book.thumbnailUrl()).dontAnimate().into(holder.imageView);
    }

}