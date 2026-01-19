package net.bloople.manga

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import net.bloople.manga.audit.AuditEventsActivity

internal class LibrariesManagementAdapter(private val fragment: LibrariesFragment) :
    RecyclerView.Adapter<LibrariesManagementAdapter.ViewHolder>() {

    override fun getItemCount(): Int = 1

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val startEditingButton: ImageButton = view.findViewById(R.id.start_editing)
        val viewAuditEventsButton: ImageButton = view.findViewById(R.id.view_audit_events)

        init {
            startEditingButton.setOnClickListener { fragment.startEditing() }

            viewAuditEventsButton.setOnClickListener {
                val intent = Intent(view.context, AuditEventsActivity::class.java)
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.libraries_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.startEditingButton.visibility = if(fragment.isEditingMode) View.GONE else View.VISIBLE
    }
}