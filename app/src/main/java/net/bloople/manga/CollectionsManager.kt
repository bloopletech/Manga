package net.bloople.manga

import android.content.Context
import android.widget.ImageButton
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import android.widget.EditText
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ListView

internal class CollectionsManager(private val activity: IndexActivity, private val adapter: BooksAdapter) {
    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var newCollection: ImageButton
    private lateinit var saveCollection: Button
    private lateinit var editCollection: ImageButton
    private lateinit var destroyCollection: ImageButton
    private lateinit var sidebar: ListView
    private var list: BookList? = null

    fun setup() {
        sidebar = activity.findViewById(R.id.sidebar)
        bookListAdapter = BookListAdapter(activity, null)
        sidebar.adapter = bookListAdapter
        updateCursor()

        sidebar.onItemClickListener = OnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            list = if(position == 0) null
            else BookList.findById(activity, parent.getItemIdAtPosition(position))

            activity.useList(list)

            view.isActivated = true

            if(list == null) {
                adapter.clearSelectedBookIds()
                adapter.setSelectable(false)

                newCollection.visibility = View.VISIBLE
                editCollection.visibility = View.GONE
                destroyCollection.visibility = View.GONE
                saveCollection.visibility = View.GONE
            }
            else {
                newCollection.visibility = View.GONE
                editCollection.visibility = View.VISIBLE
                destroyCollection.visibility = View.VISIBLE
            }
        }

        sidebar.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>?, view: View, _: Int, _: Long ->
                val nameView = view.findViewById<TextView>(R.id.name)
                val editNameView = view.findViewById<EditText>(R.id.edit_name)

                if(nameView == null) return@OnItemLongClickListener false

                editNameView.setText(nameView.text)
                nameView.visibility = View.GONE
                editNameView.visibility = View.VISIBLE

                editNameView.requestFocusFromTouch()
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editNameView, 0)

                true
            }

        newCollection = activity.findViewById(R.id.new_collection)
        newCollection.setOnClickListener { newCollection() }

        saveCollection = activity.findViewById(R.id.save_collection)
        saveCollection.setOnClickListener { updateCollection() }

        editCollection = activity.findViewById(R.id.edit_collection)
        editCollection.setOnClickListener { editCollection() }

        destroyCollection = activity.findViewById(R.id.destroy_collection)
        destroyCollection.setOnClickListener { destroyCollection() }

        newCollection.visibility = View.VISIBLE
        editCollection.visibility = View.GONE
        destroyCollection.visibility = View.GONE
        saveCollection.visibility = View.GONE
    }

    private fun newCollection() {
        list = BookList()
        list!!.name = "New Collection"
        list!!.save(activity)

        updateCursor()

        adapter.clearSelectedBookIds()
        adapter.setSelectable(true)

        newCollection.visibility = View.GONE
        editCollection.visibility = View.GONE
        saveCollection.visibility = View.VISIBLE
    }

    private fun editCollection() {
        activity.useList(null)
        adapter.setSelectedBookIds(list!!.bookIds(activity))
        adapter.setSelectable(true)
        newCollection.visibility = View.GONE
        editCollection.visibility = View.GONE
        saveCollection.visibility = View.VISIBLE
    }

    private fun updateCollection() {
        list!!.bookIds(activity, adapter.getSelectedBookIds())

        adapter.clearSelectedBookIds()
        adapter.setSelectable(false)

        activity.useList(list)

        updateCursor()

        newCollection.visibility = View.GONE
        editCollection.visibility = View.VISIBLE
        saveCollection.visibility = View.GONE
    }

    private fun destroyCollection() {
        list!!.destroy(activity)
        updateCursor()
    }

    private fun updateCursor() {
        val db = DatabaseHelper.instance(activity)
        val result = db.rawQuery("SELECT * FROM lists", arrayOf())
        bookListAdapter.changeCursor(result)
    }
}