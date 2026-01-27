package net.bloople.manga

import android.content.Context
import android.database.Cursor
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.CursorAdapter
import android.widget.TextView

class QueryAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {
    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.index_query, parent, false)
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    override fun bindView(view: View, context: Context, cursor: Cursor) {
        // Find fields to populate in inflated template
        val textView = view as TextView
        textView.text = cursor["text"]
    }

    override fun convertToString(cursor: Cursor): String {
        //returns string inserted into textview after item from drop-down list is selected.
        return cursor["text"]
    }
}