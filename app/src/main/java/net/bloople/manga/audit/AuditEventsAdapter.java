package net.bloople.manga.audit;

import android.content.Intent;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.bloople.manga.Book;
import net.bloople.manga.R;
import net.bloople.manga.ReadingActivity;

class AuditEventsAdapter extends CursorRecyclerAdapter<AuditEventsAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView whenView;
        TextView actionView;
        ImageButton openResourceView;
        TextView resourceNameView;
        TextView detailView;

        ViewHolder(View view) {
            super(view);

            whenView = view.findViewById(R.id.when);
            actionView = view.findViewById(R.id.action);

            openResourceView = view.findViewById(R.id.open_resource);
            openResourceView.setOnClickListener(v -> {
                long auditEventId = AuditEventsAdapter.this.getItemId(getAdapterPosition());
                AuditEvent event = AuditEvent.findById(openResourceView.getContext(), auditEventId);

                if(event.resourceType() == ResourceType.BOOK && event.resourceContextType() == ResourceType.LIBRARY) {
                    openBook(event.resourceContextId(), event.resourceId());
                }
            });

            resourceNameView = view.findViewById(R.id.resource_name);
            resourceNameView.setOnClickListener(v -> {
                long auditEventId = AuditEventsAdapter.this.getItemId(getAdapterPosition());
                AuditEvent event = AuditEvent.findById(openResourceView.getContext(), auditEventId);
                showFullResourceName(event.resourceName());
            });

            detailView = view.findViewById(R.id.detail);
        }

        private void openBook(long libraryId, long bookId) {
            Intent intent = new Intent(itemView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", true);
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

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy h:m a",
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

        String age = DATE_FORMAT.format(new Date(event.when()));
        holder.whenView.setText(age);

        holder.actionView.setText(event.action().toString());
        holder.resourceNameView.setText(event.resourceName());
        holder.detailView.setText(event.detail());
    }
}