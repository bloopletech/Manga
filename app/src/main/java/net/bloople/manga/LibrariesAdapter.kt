package net.bloople.manga

import android.database.Cursor
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View

internal class LibrariesAdapter(private val fragment: LibrariesFragment, cursor: Cursor?) :
    CursorRecyclerAdapter<LibrariesAdapter.ViewHolder>(cursor) {
    private var selectedLibraryId: Long = 0

    fun setCurrentLibraryId(libraryId: Long) {
        selectedLibraryId = libraryId
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        var libraryId: Long = 0
        var nameView: TextView = view.findViewById(R.id.name)
        var currentNameView: TextView = view.findViewById(R.id.current_name)

        init {
            view.setOnClickListener {
                if(fragment.isEditingMode) fragment.edit(libraryId)
                else fragment.show(
                    libraryId
                )
            }

            view.setOnLongClickListener {
                if(fragment.isEditingMode) {
                    fragment.startDrag(this@ViewHolder)
                    return@setOnLongClickListener true
                }
                else {
                    return@setOnLongClickListener false
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.library, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
        val library = Library(cursor)

        holder.libraryId = library.id

        if(library.id == selectedLibraryId) {
            holder.currentNameView.text = library.name
            holder.nameView.visibility = View.GONE
            holder.currentNameView.visibility = View.VISIBLE
        }
        else {
            holder.nameView.text = library.name
            holder.currentNameView.visibility = View.GONE
            holder.nameView.visibility = View.VISIBLE
        }
    }
}