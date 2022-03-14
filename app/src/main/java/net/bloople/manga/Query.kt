package net.bloople.manga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class Query {
    var _id = -1L
    var text: String? = null
    var createdAt: Long = 0
    var lastUsedAt: Long = 0
    var usedCount: Long = 0

    internal constructor()
    internal constructor(
        text: String?,
        createdAt: Long,
        lastUsedAt: Long,
        usedCount: Long
    ) {
        this.text = text
        this.createdAt = createdAt
        this.lastUsedAt = lastUsedAt
        this.usedCount = usedCount
    }

    internal constructor(result: Cursor) {
        _id = result.getLong(result.getColumnIndex("_id"))
        text = result.getString(result.getColumnIndex("text"))
        createdAt = result.getLong(result.getColumnIndex("created_at"))
        lastUsedAt = result.getLong(result.getColumnIndex("last_used_at"))
        usedCount = result.getLong(result.getColumnIndex("used_count"))
    }

    fun save(context: Context) {
        val values = ContentValues()
        values.put("\"text\"", text)
        values.put("created_at", createdAt)
        values.put("last_used_at", lastUsedAt)
        values.put("used_count", usedCount)
        val db = DatabaseHelper.instance(context)
        if(_id == -1L) {
            _id = db.insertOrThrow("queries", null, values)
        }
        else {
            db.update("queries", values, "_id=?", arrayOf(_id.toString()))
        }
    }

    companion object {
        fun findById(context: Context, id: Long): Query? {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM queries WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) Query(it) else null
            }
        }

        fun findByText(context: Context, text: String): Query? {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM queries WHERE \"text\"=?", arrayOf(text)).use {
                it.moveToFirst()
                return if (it.count > 0) Query(it) else null
            }
        }
    }
}