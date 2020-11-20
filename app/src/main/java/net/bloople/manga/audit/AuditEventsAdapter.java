package net.bloople.manga.audit;

import android.content.Intent;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.bloople.manga.R;
import net.bloople.manga.ReadingActivity;

class AuditEventsAdapter extends CursorRecyclerAdapter<AuditEventsAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView whenView;
        TextView actionView;
        ImageButton openResourceView;
        TextView resourceIdView;
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

                if(event.resourceType() == ResourceType.BOOK) {
                    openBook(event.resourceContextId(), event.resourceId());
                }
            });

            resourceNameView = view.findViewById(R.id.resource_name);
            detailView = view.findViewById(R.id.detail);
        }

        private void openBook(long libraryId, long bookId) {
            Intent intent = new Intent(itemView.getContext(), ReadingActivity.class);
            intent.putExtra("_id", bookId);
            intent.putExtra("resume", true);
            intent.putExtra("libraryId", libraryId);

            openResourceView.getContext().startActivity(intent);
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
        holder.resourceTypeView.setText(event.resourceType().toString());
        holder.resourceIdView.setText(String.valueOf(event.resourceId()));
        holder.resourceNameView.setText(event.resourceName());
        holder.detailView.setText(event.detail());
    }
}
