package net.bloople.manga

import android.database.Cursor
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper

class LibrariesAdapter(private val fragment: LibrariesFragment, cursor: Cursor?) :
    CursorRecyclerAdapter<LibrariesAdapter.ItemViewHolder>(cursor) {
    private lateinit var touchHelper: ItemTouchHelper
    private var selectedLibraryId: Long = 0

    fun setCurrentLibraryId(libraryId: Long) {
        selectedLibraryId = libraryId
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val holderA = viewHolder as ItemViewHolder
                val holderB = target as ItemViewHolder

                fragment.swap(holderA.libraryId, holderB.libraryId)
                notifyItemMoved(holderA.bindingAdapterPosition, holderB.bindingAdapterPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if(viewHolder is LibrariesManagementAdapter.ViewHolder) return makeMovementFlags(0, 0)
                return makeMovementFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
            }

            override fun isLongPressDragEnabled(): Boolean = false

            override fun isItemViewSwipeEnabled(): Boolean = false
        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        touchHelper.attachToRecyclerView(null)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                    touchHelper.startDrag(this@ItemViewHolder)
                    return@setOnLongClickListener true
                }
                else {
                    return@setOnLongClickListener false
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.library, parent, false)
        return ItemViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ItemViewHolder, cursor: Cursor) {
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