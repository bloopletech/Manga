package net.bloople.manga.audit;

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

        loadAuditEvents();
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

    private void loadAuditEvents() {
        ResolverTask resolver = new ResolverTask();
        resolver.execute();
    }

    class ResolverTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            SQLiteDatabase db = DatabaseHelper.instance(AuditEventsActivity.this);

            Cursor cursor = db.query(
                "audit_events",
                null,
                null,
                null,
                null,
                null,
                "\"when\" DESC"
            );

            cursor.moveToFirst();

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            adapter.swapCursor(cursor);
        }
    }
}
