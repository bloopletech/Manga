package net.bloople.manga

import android.content.Context
import android.database.Cursor
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.EditText
import android.view.inputmethod.EditorInfo
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CursorAdapter

internal class BookListAdapter(context: Context?, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {
    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.sidebar_item, parent, false)
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    override fun bindView(view: View, context: Context, cursor: Cursor) {
        // Find fields to populate in inflated template
        val nameView = view.findViewById<TextView>(R.id.name)
        val editNameView = view.findViewById<EditText>(R.id.edit_name)

        val listId = cursor.getLong(cursor.getColumnIndex("_id"))
        val name = cursor.getString(cursor.getColumnIndex("name"))
        nameView.text = name

        nameView.visibility = View.VISIBLE
        editNameView.visibility = View.GONE

        editNameView.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if(actionId != EditorInfo.IME_ACTION_DONE) return@setOnEditorActionListener false

            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editNameView.windowToken, 0)
            editNameView.clearFocus()

            val list = BookList.findById(context, listId)
            list.name(editNameView.text.toString())
            list.save(context)

            nameView.text = editNameView.text.toString()
            nameView.visibility = View.VISIBLE
            editNameView.visibility = View.GONE

            true
        }

        editNameView.setOnTouchListener { v: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2

            if(event.action == MotionEvent.ACTION_UP) {
                val clickIndex = editNameView.right -
                    editNameView.compoundDrawables[DRAWABLE_RIGHT].bounds.width()

                if(event.rawX < clickIndex) return@setOnTouchListener false

                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editNameView.windowToken, 0)
                editNameView.clearFocus()

                nameView.visibility = View.VISIBLE
                editNameView.visibility = View.GONE

                return@setOnTouchListener true
            }

            false
        }
    }
}