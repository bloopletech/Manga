package net.bloople.manga

import android.database.Cursor
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton

internal class LibrariesAdapter(private val fragment: LibrariesFragment, cursor: Cursor?) :
    CursorRecyclerAdapter<RecyclerView.ViewHolder>(cursor) {
    private var selectedLibraryId: Long = 0

    fun setCurrentLibraryId(libraryId: Long) {
        selectedLibraryId = libraryId
        notifyDataSetChanged()
    }

    fun onEditModeChanged() {
        notifyItemChanged(super.itemCount)
    }

    override fun getItemCount(): Int = super.itemCount + 1

    override fun getItemViewType(position: Int): Int = if (position == super.itemCount) TYPE_MANAGEMENT else TYPE_ITEM

    internal inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                    fragment.startDrag(this@ItemViewHolder)
                    return@setOnLongClickListener true
                }
                else {
                    return@setOnLongClickListener false
                }
            }
        }
    }

    internal inner class ManagementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var startEditingButton: ImageButton = view.findViewById(R.id.start_editing)

        init {
            startEditingButton.setOnClickListener {
                fragment.startEditing()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.library, parent, false)
                ItemViewHolder(view)
            }
            TYPE_MANAGEMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.libraries_management, parent, false)
                ManagementViewHolder(view)
            }
            else -> throw RuntimeException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            TYPE_ITEM -> super.onBindViewHolder(holder, position)
            TYPE_MANAGEMENT -> onBindManagementViewHolder(holder as ManagementViewHolder, position)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, cursor: Cursor) {
        val itemHolder = holder as ItemViewHolder

        val library = Library(cursor)

        itemHolder.libraryId = library.id

        if(library.id == selectedLibraryId) {
            itemHolder.currentNameView.text = library.name
            itemHolder.nameView.visibility = View.GONE
            itemHolder.currentNameView.visibility = View.VISIBLE
        }
        else {
            itemHolder.nameView.text = library.name
            itemHolder.currentNameView.visibility = View.GONE
            itemHolder.nameView.visibility = View.VISIBLE
        }
    }

    fun onBindManagementViewHolder(holder: ManagementViewHolder, position: Int) {
        holder.startEditingButton.visibility = if(fragment.isEditingMode) View.GONE else View.VISIBLE
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_MANAGEMENT = 1
    }
}