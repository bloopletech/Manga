package net.bloople.manga.audit;

import android.content.Intent;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.bloople.manga.Book;
import net.bloople.manga.CursorRecyclerAdapter;
import net.bloople.manga.IndexActivity;
import net.bloople.manga.LibraryService;
import net.bloople.manga.R;
import net.bloople.manga.ReadingActivity;

class AuditEventsAdapter extends CursorRecyclerAdapter<AuditEventsAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder {
        AuditEvent event;
        TextView whenView;
        TextView actionView;
        ImageView imageView;
        ImageButton openResourceView;
        TextView resourceNameView;
        TextView detailView;

        ViewHolder(View view) {
            super(view);

            whenView = view.findViewById(R.id.when);
            actionView = view.findViewById(R.id.action);

            imageView = view.findViewById(R.id.image_view);
            imageView.setOnClickListener(v -> {
                openResource(event);
            });

            openResourceView = view.findViewById(R.id.open_resource);
            openResourceView.setOnClickListener(v -> {
                openResource(event);
            });

            resourceNameView = view.findViewById(R.id.resource_name);
            resourceNameView.setOnClickListener(v -> {
                showFullResourceName(event.resourceName());
            });

            detailView = view.findViewById(R.id.detail);
        }

        private void openResource(AuditEvent event) {
            if(event.resourceType() == ResourceType.BOOK && event.resourceContextType() == ResourceType.LIBRARY) {
                openBook(event.resourceContextId(), event.resourceId());
            }
            else if(event.resourceType() == ResourceType.LIBRARY) {
                openLibrary(event.resourceId());
            }
        }

        private void openBook(long libraryId, long bookId) {
            Intent intent = new Intent(openResourceView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", true);
            intent.putExtra("libraryId", libraryId);

            openResourceView.getContext().startActivity(intent);
        }

        private void openLibrary(long libraryId) {
            Intent intent = new Intent(openResourceView.getContext(), IndexActivity.class);
            intent.putExtra("libraryId", libraryId);

            openResourceView.getContext().startActivity(intent);
        }

        private void showFullResourceName(String resourceName) {
            View popupView = LayoutInflater.from(resourceNameView.getContext()).inflate(
                R.layout.audit_audit_event_resource_name_popup,
                null,
                false
            );
            TextView resourceNamePopupView = popupView.findViewById(R.id.resource_name);
            resourceNamePopupView.setText(resourceName);

            int popupWidth = resourceNameView.getWidth() + resourceNamePopupView.getPaddingStart() + resourceNamePopupView.getPaddingEnd();
            PopupWindow popup = new PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setFocusable(true);
            popup.setOutsideTouchable(true);
            popup.setElevation(24);

            popupView.setOnClickListener(v -> {
                popup.dismiss();
            });

            popup.showAsDropDown(
                resourceNameView,
                -resourceNamePopupView.getPaddingStart(),
                -resourceNameView.getHeight(),
                Gravity.TOP | Gravity.START
            );
        }
    }

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy h:mm a",
        Locale.getDefault());

    AuditEventsAdapter(Cursor cursor) {
        super(cursor);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AuditEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.audit_audit_event,
            parent,
            false
        );

        return new AuditEventsAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AuditEventsAdapter.ViewHolder holder, Cursor cursor) {
        AuditEvent event = new AuditEvent(cursor);
        holder.event = event;

        String age = DATE_FORMAT.format(new Date(event.when()));
        holder.whenView.setText(age);

        holder.actionView.setText(event.action().toString());
        holder.resourceNameView.setText(event.resourceName());
        holder.detailView.setText(event.detail());

        Glide.clear(holder.imageView);
        holder.imageView.setImageDrawable(null);

        if(event.resourceType() == ResourceType.BOOK && event.resourceContextType() == ResourceType.LIBRARY) {
            LibraryService.ensureLibrary(holder.imageView.getContext(), event.resourceContextId(), library -> {
                holder.openResourceView.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);

                Book book = library.books().get(event.resourceId());

                final double viewWidthToBitmapWidthRatio = (double)holder.imageView.getLayoutParams().width / 197.0;
                holder.imageView.getLayoutParams().height = (int)(310.0 * viewWidthToBitmapWidthRatio);

                Glide.with(holder.imageView.getContext()).load(book.thumbnailUrl()).dontAnimate().into(holder.imageView);
            });
        }
        else {
            holder.openResourceView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
        }
    }
}
