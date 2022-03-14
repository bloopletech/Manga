package net.bloople.manga.audit

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import net.bloople.manga.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.database.Cursor
import android.os.AsyncTask

class AuditEventsActivity : AppCompatActivity() {
    private lateinit var auditEventsView: RecyclerView
    private lateinit var adapter: AuditEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.audit_activity_audit_events)

        auditEventsView = findViewById(R.id.audit_events)
        auditEventsView.setLayoutManager(LinearLayoutManager(this))

        adapter = AuditEventsAdapter(null)
        auditEventsView.setAdapter(adapter)

        val intent = intent
        val resourceId = intent.getLongExtra("resourceId", -1)
        loadAuditEvents(resourceId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //libraryId = savedInstanceState.getLong("libraryId");
        //loadLibrary();
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //savedInstanceState.putLong("libraryId", libraryId);
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        finish()
    }

    private fun loadAuditEvents(resourceId: Long) {
        val resolver = ResolverTask()
        resolver.execute(resourceId)
    }

    internal inner class ResolverTask : AsyncTask<Long?, Void?, Cursor>() {
        override fun doInBackground(vararg resourceIds: Long?): Cursor {
            val resourceId = resourceIds[0]

            val db = DatabaseHelper.instance(this@AuditEventsActivity)

            val cursor: Cursor = if(resourceId != -1L) {
                db.query(
                    "audit_events",
                    null,
                    "resource_id = ?", arrayOf(resourceId.toString()),
                    null,
                    null,
                    "\"when\" DESC"
                )
            }
            else {
                db.query(
                    "audit_events",
                    null,
                    null,
                    null,
                    null,
                    null,
                    "\"when\" DESC"
                )
            }
            cursor.moveToFirst()
            return cursor
        }

        override fun onPostExecute(cursor: Cursor) {
            adapter!!.swapCursor(cursor)
        }
    }
}