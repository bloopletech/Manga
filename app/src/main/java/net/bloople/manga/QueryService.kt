package net.bloople.manga

import android.content.Context
import android.widget.AutoCompleteTextView
import android.widget.FilterQueryProvider
import net.bloople.manga.db.DatabaseHelper

class QueryService(context: Context, searchField: AutoCompleteTextView) {
    private val adapter: QueryAdapter = QueryAdapter(context, null)

    init {
        adapter.filterQueryProvider = FilterQueryProvider { constraint: CharSequence? ->
            val db = DatabaseHelper.instance()
            if(constraint != null) {
                db.rawQuery(
                    "SELECT _id, text FROM queries WHERE text LIKE ? OR text LIKE ? ORDER BY last_used_at DESC LIMIT $FILTER_QUERIES_LIMIT",
                    arrayOf("$constraint%", "\"$constraint%")
                )
            }
            else {
                db.rawQuery(
                    "SELECT _id, text FROM queries ORDER BY last_used_at DESC LIMIT $FILTER_QUERIES_LIMIT",
                    emptyArray()
                )
            }
        }

        searchField.setAdapter(adapter)
        searchField.setOnDismissListener { adapter.cursor.close() }
    }

    fun onSearch(text: String) {
        val now = System.currentTimeMillis()
        var existing = Query.findByText(text)
        if(existing == null) {
            existing = Query()
            existing.text = text
            existing.createdAt = now
        }
        existing.lastUsedAt = now
        existing.usedCount++
        existing.save()
    }

    companion object {
        const val FILTER_QUERIES_LIMIT = 100
    }
}