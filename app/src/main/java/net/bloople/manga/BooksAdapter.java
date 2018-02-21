package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
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

    void update(ArrayList<Long> inBookIds) {
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(selectable) return false;

                    long bookId = BooksAdapter.this.getItemId(getAdapterPosition());
                    openBook(bookId, false);

                    return true;
                }
            });

            pageCountView = (TextView)view.findViewById(R.id.page_count_view);
            textView = (TextView)view.findViewById(R.id.text_view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
            selectableView = (ImageView)view.findViewById(R.id.selectable);
        }

        private void openBook(long bookId, boolean resume) {
            BookMetadata metadata = BookMetadata.findOrCreateByBookId(itemView.getContext(), bookId);
            metadata.lastOpenedAt(System.currentTimeMillis());
            metadata.save(itemView.getContext());

            Intent intent = new Intent(itemView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", resume);

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
        Book book = MangaApplication.allBooks.get(bookId);

        holder.selectableView.setVisibility(selectable ? View.VISIBLE : View.INVISIBLE);
        if(selectable) holder.itemView.setActivated(selectedBookIds.contains(bookId));

        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.setText(book.title());

        holder.pageCountView.setText(String.format("%,d", book.pages()));

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Uri uri = MangaApplication.root().buildUpon().appendEncodedPath(book.thumbnailUrl()).build();

        Glide.with(holder.imageView.getContext()).load(uri).dontAnimate().into(holder.imageView);
    }

}