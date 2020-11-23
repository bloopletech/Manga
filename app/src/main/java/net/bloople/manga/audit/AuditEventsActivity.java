package net.bloople.manga.audit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.bloople.manga.R;

public class AuditEventsActivity extends AppCompatActivity {
    private RecyclerView auditEventsView;
    private AuditEventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.audit_activity_audit_events);

        auditEventsView = findViewById(R.id.audit_events);
        auditEventsView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AuditEventsAdapter(null);
        auditEventsView.setAdapter(adapter);

        final Intent intent = getIntent();
        long resourceId = intent.getLongExtra("resourceId", -1);
        loadAuditEvents(resourceId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //libraryId = savedInstanceState.getLong("libraryId");
        //loadLibrary();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putLong("libraryId", libraryId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadAuditEvents(long resourceId) {
        ResolverTask resolver = new ResolverTask();
        resolver.execute(resourceId);
    }

    class ResolverTask extends AsyncTask<Long, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Long... resourceIds) {
            long resourceId = resourceIds[0];

            SQLiteDatabase db = DatabaseHelper.instance(AuditEventsActivity.this);

            Cursor cursor;
            if(resourceId != -1) {
                cursor = db.query(
                    "audit_events",
                    null,
                    "resource_id = ?",
                    new String[] { String.valueOf(resourceId) },
                    null,
                    null,
                    "\"when\" DESC"
                );
            }
            else {
                cursor = db.query(
                    "audit_events",
                    null,
                    null,
                    null,
                    null,
                    null,
                    "\"when\" DESC"
                );
            }

            cursor.moveToFirst();

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            adapter.swapCursor(cursor);
        }
    }
}
