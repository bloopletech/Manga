package net.bloople.awdiobooks

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DatabaseAdapter(private val db: SQLiteDatabase, private val table: String) {
    fun insert(values: ContentValues): Long {
        return db.insert(table, null, values)
    }

    fun update(values: ContentValues, id: Long) {
        db.update(table, values, "_id=?", arrayOf(id.toString()))
    }

    fun delete(id: Long) {
        db.delete(table, "_id=?", arrayOf(id.toString()))
    }

    fun <T> find(id: Long, build: (Cursor) -> T): T {
        db.rawQuery("SELECT * FROM $table WHERE _id=?", arrayOf(id.toString())).use {
            it.moveToFirst()
            return if (it.count > 0) build(it) else throw NoSuchElementException("$table with id $id not found")
        }
    }

    fun <T> find(query: String, vararg args: String, build: (Cursor) -> T): T {
        db.rawQuery(query, args).use {
            it.moveToFirst()
            return if (it.count > 0) build(it) else throw NoSuchElementException("$table record not found")
        }
    }

    fun <T> findBy(query: String, vararg args: String, build: (Cursor) -> T): T? {
        db.rawQuery(query, args).use {
            it.moveToFirst()
            return if (it.count > 0) build(it) else null
        }
    }

    fun <T> query(query: String, vararg args: String, process: (Cursor) -> T): T {
        db.rawQuery(query, args).use { return process(it) }
    }

    fun count(): Long {
        db.rawQuery("SELECT COUNT(*) FROM $table", null).use {
            it.moveToFirst()
            return it.getLong(0)
        }
    }
}