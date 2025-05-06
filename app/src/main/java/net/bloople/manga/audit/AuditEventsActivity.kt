package net.bloople.manga.audit

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import net.bloople.manga.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.database.Cursor
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider

class AuditEventsActivity : AppCompatActivity() {
    private lateinit var model: AuditEventsViewModel

    private lateinit var auditEventsView: RecyclerView
    private lateinit var adapter: AuditEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.audit_activity_audit_events)

        model = ViewModelProvider(this)[AuditEventsViewModel::class.java]

        auditEventsView = findViewById(R.id.audit_events)
        auditEventsView.layoutManager = LinearLayoutManager(this)

        adapter = AuditEventsAdapter(null)
        auditEventsView.adapter = adapter

        model.searchResults.observe(this) { searchResults: Cursor -> adapter.swapCursor(searchResults) }

        val resourceId = intent.getLongExtra("resourceId", -1)

        model.setResourceId(if(resourceId != -1L) resourceId else null)
    }

    override fun onBackPressed() {
        finish()
    }
}