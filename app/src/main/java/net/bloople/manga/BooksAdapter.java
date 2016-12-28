package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i on 9/07/2016.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private MultiSelector multiSelector;

    public BooksAdapter(MultiSelector multiSelector) {
        this.multiSelector = multiSelector;
    }

    class ViewHolder extends SwappingHolder implements View.OnClickListener {
        public TextView pageCountView;
        public TextView textView;
        public ImageView imageView;
        public ImageView selectableView;
        public ViewHolder(View view) {
            super(view, multiSelector);

            view.setOnClickListener(this);

            pageCountView = (TextView)view.findViewById(R.id.page_count_view);
            textView = (TextView)view.findViewById(R.id.text_view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
            selectableView = (ImageView)view.findViewById(R.id.selectable);
        }

        @Override
        public void onClick(View view) {
            if(!multiSelector.tapSelection(ViewHolder.this)){
                Intent intent = new Intent(view.getContext(), ReadingActivity.class);
                intent.putExtra("key", at(getAdapterPosition()).key());

                view.getContext().startActivity(intent);
            }
        }


        public void setSelectable(boolean isSelectable) {
            super.setSelectable(isSelectable);
            selectableView.setVisibility(isSelectable ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private List<Book> books = new ArrayList<>();

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
        Book book = books.get(position);

        String title = book.title().replaceAll("\\s+", " ");
        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.setText(title);

        holder.pageCountView.setText(String.format("%,d", book.pages()));

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Uri uri = MangaApplication.root().buildUpon().appendEncodedPath(book.thumbnailUrl()).build();

        Glide.with(holder.imageView.getContext()).load(uri).dontAnimate().into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return books.size();
    }

    public void update(ArrayList<Book> inBooks) {
        books.clear();
        books.addAll(inBooks);
        notifyDataSetChanged();
    }

    public Book at(int position) {
        return books.get(position);
    }
}