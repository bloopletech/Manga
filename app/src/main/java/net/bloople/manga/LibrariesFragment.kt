package net.bloople.manga

import android.content.Context
import net.bloople.manga.Library.Companion.findHighestPosition
import net.bloople.manga.Library.Companion.findById
import net.bloople.manga.LibraryEditFragment.OnLibraryEditFinishedListener
import android.widget.ImageButton
import androidx.recyclerview.widget.ItemTouchHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import net.bloople.manga.audit.AuditEventsActivity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibrariesFragment : Fragment(), OnLibraryEditFinishedListener {
    private var listener: OnLibrarySelectedListener? = null
    private lateinit var librariesAdapter: LibrariesAdapter
    private lateinit var databaseManagementFragment: DatabaseManagementFragment

    private lateinit var viewAuditEventsButton: ImageButton
    private lateinit var finishEditingButton: ImageButton
    private lateinit var newLibraryButton: ImageButton
    private lateinit var clearCacheButton: ImageButton
    private lateinit var touchHelper: ItemTouchHelper
    var isEditingMode = false
        private set

    internal interface OnLibrarySelectedListener {
        fun onLibrarySelected(libraryId: Long)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnLibrarySelectedListener
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.libraries_fragment, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseManagementFragment = childFragmentManager
            .findFragmentById(R.id.database_management_framework) as DatabaseManagementFragment

        val librariesView: RecyclerView = view.findViewById(R.id.libraries)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        librariesView.layoutManager = layoutManager

        librariesAdapter = LibrariesAdapter(this, null)
        librariesView.adapter = librariesAdapter

        viewAuditEventsButton = view.findViewById(R.id.view_audit_events)
        viewAuditEventsButton.setOnClickListener {
            val intent = Intent(context, AuditEventsActivity::class.java)
            startActivity(intent)
        }

        finishEditingButton = view.findViewById(R.id.finish_editing)
        finishEditingButton.setOnClickListener {
            isEditingMode = false
            librariesAdapter.onEditModeChanged()
            clearCacheButton.visibility = View.GONE
            databaseManagementFragment.requireView().visibility = View.GONE
            finishEditingButton.visibility = View.GONE
            newLibraryButton.visibility = View.GONE
        }

        newLibraryButton = view.findViewById(R.id.new_library)
        newLibraryButton.setOnClickListener { create() }

        clearCacheButton = view.findViewById(R.id.clear_cache)
        clearCacheButton.setOnClickListener { clearCache() }

        touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if(viewHolder is LibrariesAdapter.ManagementViewHolder || target is LibrariesAdapter.ManagementViewHolder) return false
                val holderA = viewHolder as LibrariesAdapter.ItemViewHolder
                val holderB = target as LibrariesAdapter.ItemViewHolder

                swap(holderA.libraryId, holderB.libraryId)
                librariesAdapter.notifyItemMoved(holderA.bindingAdapterPosition, holderB.bindingAdapterPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // no-op
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }
        })
        touchHelper.attachToRecyclerView(librariesView)

        updateCursor()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onLibraryEditFinished(library: Library?) {
        updateCursor()
    }

    internal fun startDrag(holder: LibrariesAdapter.ItemViewHolder?) {
        touchHelper.startDrag(holder!!)
    }

    fun setCurrentLibraryId(libraryId: Long) {
        librariesAdapter.setCurrentLibraryId(libraryId)
    }

    fun show(libraryId: Long) {
        listener!!.onLibrarySelected(libraryId)
    }

    fun startEditing() {
        newLibraryButton.visibility = View.VISIBLE
        finishEditingButton.visibility = View.VISIBLE
        databaseManagementFragment.requireView().visibility = View.VISIBLE
        clearCacheButton.visibility = View.VISIBLE
        isEditingMode = true
        librariesAdapter.onEditModeChanged()
    }

    fun edit(libraryId: Long) {
        val childFragment = LibraryEditFragment.newInstance(libraryId)
        childFragment.show(childFragmentManager, null)
    }

    private fun create() {
        val library = Library()
        library.name = "New Library"
        library.position = findHighestPosition(requireContext()) + 1
        library.root = "http://example.com/"
        library.save(requireContext())
        updateCursor()
    }

    fun swap(libraryAId: Long, libraryBId: Long) {
        val libraryA = findById(requireContext(), libraryAId)
        val libraryB = findById(requireContext(), libraryBId)
        val aPosition = libraryA!!.position
        libraryA.position = libraryB!!.position
        libraryB.position = aPosition
        libraryA.save(requireContext())
        libraryB.save(requireContext())

        updateCursor()
    }

    private fun updateCursor() {
        val db = DatabaseHelper.instance(requireContext())
        val result = db.rawQuery("SELECT * FROM library_roots ORDER BY position ASC", arrayOf())
        result.moveToFirst()
        librariesAdapter.swapCursor(result)
    }

    private fun clearCache() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                requireContext().cacheDir.deleteRecursively()
                requireContext().externalCacheDirs.filterNotNull().forEach { it.deleteRecursively() }
            }

            Toast.makeText(requireContext(), "Cache Cleared", Toast.LENGTH_LONG).show()
        }
        catch(e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
    }
}