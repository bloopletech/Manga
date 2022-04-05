package net.bloople.manga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.util.ArrayList

class BookList {
    var _id = -1L
    var name: String? = null

    constructor()
    constructor(result: Cursor) {
        _id = result["_id"]
        name = result["name"]
    }

    fun bookIds(context: Context): ArrayList<Long> {
        val db = DatabaseHelper.instance(context)
        db.rawQuery("SELECT book_id FROM lists_books WHERE list_id=?", arrayOf(_id.toString())).use {
            it.moveToFirst()
            val bookIds = ArrayList<Long>()
            while(it.moveToNext()) bookIds.add(it["book_id"])
            return bookIds
        }
    }

    fun bookIds(context: Context, bookIds: ArrayList<Long>) {
        val db = DatabaseHelper.instance(context)
        db.delete("lists_books", "list_id=?", arrayOf(_id.toString()))
        for(bookId in bookIds) {
            val values = ContentValues()
            values.put("list_id", _id)
            values.put("book_id", bookId)
            db.insert("lists_books", null, values)
        }
    }

    fun save(context: Context) {
        val values = ContentValues()
        values.put("name", name)
        val db = DatabaseHelper.instance(context)
        if(_id == -1L) {
            _id = db.insert("lists", null, values)
        }
        else {
            db.update("lists", values, "_id=?", arrayOf(_id.toString()))
        }
    }

    fun destroy(context: Context) {
        val db = DatabaseHelper.instance(context)
        db.delete("lists", "_id=?", arrayOf(_id.toString()))
    }

    companion object {
        fun findById(context: Context, id: Long): BookList? {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM lists WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) BookList(it) else null
            }
        }
    }
}