package net.bloople.manga;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
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
        setSelectedBookIds(new ArrayList<>());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookIds.size();
    }

    void update(Library library, ArrayList<Long> inBookIds) {
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

            pageCountView = view.findViewById(R.id.page_count_view);
            textView = view.findViewById(R.id.text_view);
            imageView = view.findViewById(R.id.image_view);
            selectableView = view.findViewById(R.id.selectable);

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

            textView.setOnClickListener(v -> {
                long bookId = BooksAdapter.this.getItemId(getAdapterPosition());
                showFullBookTitle(bookId);
            });

            textView.setOnLongClickListener(v -> {
                long bookId = BooksAdapter.this.getItemId(getAdapterPosition());
                showBookTags(bookId);

                return true;
            });
        }

        private void openBook(long bookId, boolean resume) {
            Intent intent = new Intent(itemView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", resume);
            intent.putExtra("libraryId", library.id());

            itemView.getContext().startActivity(intent);
        }

        private void showFullBookTitle(long bookId) {
            Book book = library.books().get(bookId);

            View popupView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.index_book_title_popup, null, false);
            TextView bookTitleView = popupView.findViewById(R.id.book_title);
            bookTitleView.setText(book.title());

            int popupWidth = textView.getWidth() + bookTitleView.getPaddingStart() + bookTitleView.getPaddingEnd();
            PopupWindow popup = new PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setFocusable(true);
            popup.setOutsideTouchable(true);
            popup.setElevation(24);

            popupView.setOnClickListener(v -> {
                popup.dismiss();
            });

            popup.showAsDropDown(textView, -bookTitleView.getPaddingStart(), -textView.getHeight(), Gravity.TOP | Gravity.START);
        }

        private void showBookTags(long bookId) {
            IndexActivity indexActivity = (IndexActivity)itemView.getContext();

            Book book = library.books().get(bookId);
            TagChooserFragment tagChooser = TagChooserFragment.newInstance(book.tags.toArray(new String[0]));
            tagChooser.show(indexActivity.getSupportFragmentManager(), "tag_chooser");
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_book_view, parent, false);

        final double viewWidthToBitmapWidthRatio = (double)parent.getWidth() / 4.0 / 197.0;
        view.getLayoutParams().height = (int)(310.0 * viewWidthToBitmapWidthRatio);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        long bookId = getItemId(position);
        Book book = library.books().get(bookId);

        holder.selectableView.setVisibility(selectable ? View.VISIBLE : View.INVISIBLE);
        if(selectable) holder.itemView.setActivated(selectedBookIds.contains(bookId));

        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.setText(book.title());

        holder.pageCountView.setText(String.format("%,d", book.pages));

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        Glide.with(holder.imageView.getContext()).load(book.thumbnailUrl()).dontAnimate().into(holder.imageView);
    }

}