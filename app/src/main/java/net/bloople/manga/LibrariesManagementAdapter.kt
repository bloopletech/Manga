package net.bloople.manga

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import net.bloople.manga.audit.AuditEventsActivity

internal class LibrariesManagementAdapter(private val fragment: LibrariesFragment) :
    RecyclerView.Adapter<LibrariesManagementAdapter.ViewHolder>() {
    fun onEditModeChanged() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 1

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), PopupMenu.OnMenuItemClickListener {
        val startEditingButton: ImageButton = view.findViewById(R.id.start_editing)
        val databaseManagementFragment: DatabaseManagementFragment = view.findFragmentById(R.id.database_management_framework)
        //val clearCacheButton: ImageButton = view.findViewById(R.id.clear_cache)
        val viewAuditEventsButton: ImageButton = view.findViewById(R.id.view_audit_events)
        val openPopupMenuButton: ImageButton = view.findViewById(R.id.open_popup_menu)


        init {
            startEditingButton.setOnClickListener {
                fragment.startEditing()
            }

            //clearCacheButton.setOnClickListener { fragment.clearCache() }

            viewAuditEventsButton.setOnClickListener {
                val intent = Intent(view.context, AuditEventsActivity::class.java)
                view.context.startActivity(intent)
            }

            openPopupMenuButton.setOnClickListener {
                PopupMenu(view.rootView.context, openPopupMenuButton).apply {
                    setOnMenuItemClickListener(this@ViewHolder)
                    inflate(R.menu.libraries_management_menu)
                    show()
                }
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.import_database -> {
                    databaseManagementFragment.startImport()
                    true
                }
                R.id.export_database -> {
                    databaseManagementFragment.startExport()
                    true
                }
                R.id.clear_cache -> {
                    fragment.clearCache()
                    true
                }
                else -> false
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.libraries_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visible = if(fragment.isEditingMode) View.GONE else View.VISIBLE
        holder.startEditingButton.visibility = visible
        holder.databaseManagementFragment.requireView().visibility = visible
        //holder.clearCacheButton.visibility = visible
        holder.viewAuditEventsButton.visibility = visible
    }
}