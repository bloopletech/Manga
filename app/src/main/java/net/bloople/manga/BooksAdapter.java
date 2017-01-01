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

/**
 * Created by i on 9/07/2016.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private ArrayList<Long> bookIds = new ArrayList<>();
    private ArrayList<Long> selectedBookIds = new ArrayList<>();
    private boolean selectable = false;

    public BooksAdapter() {
        setHasStableIds(true);
    }

    public long getItemId(int position) {
        return bookIds.get(position);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean isSelectable) {
        selectable = isSelectable;
        notifyDataSetChanged();
    }

    public ArrayList<Long> getSelectedBookIds() {
        return selectedBookIds;
    }

    public void setSelectedBookIds(ArrayList<Long> selectedBookIds) {
        this.selectedBookIds = selectedBookIds;
        notifyDataSetChanged();
    }

    public void clearSelectedBookIds() {
        setSelectedBookIds(new ArrayList<Long>());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookIds.size();
    }

    public void update(ArrayList<Long> inBookIds) {
        bookIds = inBookIds;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pageCountView;
        public TextView textView;
        public ImageView imageView;
        public ImageView selectableView;
        public ViewHolder(View view) {
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
                        Intent intent = new Intent(v.getContext(), ReadingActivity.class);
                        intent.putExtra("_id", bookId);

                        v.getContext().startActivity(intent);
                    }
                }
            });

            pageCountView = (TextView)view.findViewById(R.id.page_count_view);
            textView = (TextView)view.findViewById(R.id.text_view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
            selectableView = (ImageView)view.findViewById(R.id.selectable);
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

        String title = book.title().replaceAll("\\s+", " ");
        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.setText(title);

        holder.pageCountView.setText(String.format("%,d", book.pages()));

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Uri uri = MangaApplication.root().buildUpon().appendEncodedPath(book.thumbnailUrl()).build();

        Glide.with(holder.imageView.getContext()).load(uri).dontAnimate().into(holder.imageView);
    }

}