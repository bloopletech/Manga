package net.bloople.manga

import android.content.ContentValues
import android.database.Cursor
import net.bloople.awdiobooks.DatabaseAdapter

class Query {
    var id = -1L
    var text: String? = null
    var createdAt: Long = 0
    var lastUsedAt: Long = 0
    var usedCount: Long = 0

    constructor()
    constructor(result: Cursor) {
        id = result["_id"]
        text = result["text"]
        createdAt = result["created_at"]
        lastUsedAt = result["last_used_at"]
        usedCount = result["used_count"]
    }

    fun save() {
        val values = ContentValues()
        values.put("\"text\"", text)
        values.put("created_at", createdAt)
        values.put("last_used_at", lastUsedAt)
        values.put("used_count", usedCount)
        if(id == -1L) {
            id = dba.insert(values)
        }
        else {
            dba.update(values, id)
        }
    }

    companion object {
        private val dba: DatabaseAdapter
            get() = DatabaseAdapter(DatabaseHelper.instance(), "queries")

        fun find(id: Long) = dba.find(id) { Query(it) }

        fun findByText(text: String): Query? =
            dba.findBy("SELECT * FROM queries WHERE \"text\"=?", text) { Query(it) }
    }
}