package net.bloople.manga;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by i on 9/07/2016.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View view, final OnItemClickListener clickListener) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });

            imageView = (ImageView)view.findViewById(R.id.image_view);
        }
    }

    private List<Book> books;
    private OnItemClickListener mOnItemClickListener;

    public BooksAdapter(List<Book> inBooks) {
        books = inBooks;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent,
                false);

        final double viewWidthToBitmapWidthRatio = (double)parent.getWidth() / 3.0 / 197.0;
        view.getLayoutParams().height = (int)(310.0 * viewWidthToBitmapWidthRatio);

        return new ViewHolder(view, mOnItemClickListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Book book = books.get(position);
        Uri uri = MangaApplication.root().buildUpon().appendEncodedPath(book.getThumbnailUrl()).build();

        Glide.with(holder.imageView.getContext()).load(uri.toString()).into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return books.size();
    }

    public Book getModel(int index) {
        return books.get(index);
    }
}