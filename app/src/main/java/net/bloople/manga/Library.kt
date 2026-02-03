package net.bloople.manga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import net.bloople.manga.db.DatabaseAdapter
import java.util.HashMap
import androidx.core.net.toUri
import net.bloople.manga.db.DatabaseHelper

class Library {
    var id = -1L
    var name: String? = null
    var position = 0
    var root: String? = null
        set(value) { field = value!!.toUri().toString() }
    var username: String? = null
    var password: String? = null
    val books = HashMap<Long, Book>()

    val rootUrl: MangosUrl by lazy { MangosUrl(root!!, username, password) }
    val mangos: MangosUrl by lazy { rootUrl / ".mangos" }
    val dataUrl: MangosUrl by lazy { mangos / DATA_JSON_PATH }
    val thumbnailsUrl: MangosUrl by lazy { mangos / "img" / "thumbnails" }

    constructor()
    constructor(result: Cursor) {
        id = result["_id"]
        name = result["name"]
        position = result["position"]
        root = result["root"]
        username = result["username"]
        password = result["password"]
    }

    fun inflate(deflatedBooks: List<Book>) {
        books.clear()
        books.putAll(deflatedBooks.associateBy { it.id })
        for(book in deflatedBooks) book.library = this
    }

    fun save() {
        val values = ContentValues()
        values.put("name", name)
        values.put("position", position)
        values.put("root", root)
        values.put("username", username)
        values.put("password", password)
        if(id == -1L) {
            id = dba.insert(values)
        }
        else {
            dba.update(values, id)
        }
    }

    fun destroy() {
        dba.delete(id)
    }

    fun isEmpty() = id == -1L
    fun isPresent() = !isEmpty()

    companion object {
        private val dba: DatabaseAdapter
            get() = DatabaseAdapter(DatabaseHelper.instance(), "library_roots")

        private const val DATA_JSON_PATH = "data.json"

        fun find(id: Long) = dba.find(id) { Library(it) }

        fun findDefault(): Library? =
            dba.findBy("SELECT * FROM library_roots ORDER BY position ASC LIMIT 1") { Library(it) }

        fun findHighestPosition(): Int =
            dba.findBy("SELECT position FROM library_roots ORDER BY position DESC LIMIT 1") { it["position"] } ?: 0

        val EMPTY = Library()
    }
}