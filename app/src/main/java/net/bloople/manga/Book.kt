package net.bloople.manga

import java.util.ArrayList

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Book(
    val path: String,
    val pagePaths: String,
    val pages: Int,
    val publishedOn: Int,
    val key: String,
    val tags: List<String>
) {
    @Transient
    private var library: Library? = null
    @Transient
    private var title: String? = null
    @Transient
    private var normalisedTitle: String? = null
    @Transient
    private var _id: Long = 0
    @Transient
    private var pagePathsList: ArrayList<String>? = null

    fun library(): Library? {
        return library
    }

    fun thumbnailUrl(): MangosUrl {
        return library!!.mangos().withAppendedPath("/img/thumbnails/$key.jpg")
    }

    fun title(): String? {
        return title
    }

    fun normalisedTitle(): String? {
        return normalisedTitle
    }

    fun id(): Long {
        return _id
    }

    private fun pagePaths(): ArrayList<String>? {
        if(pagePathsList == null) pagePathsList = PagesInflater(pagePaths).inflate()
        return pagePathsList
    }

    fun pageUrl(index: Int): MangosUrl {
        return library!!.rootUrl().withAppendedPath("/" + path + "/" + pagePaths()!![index])
    }

    fun inflate(library: Library) {
        this.library = library
        _id = key.substring(0, 15).toLong(16) //Using substring of key would be dangerous for large N
        normalisedTitle = path.replace("[^A-Za-z0-9]+".toRegex(), "").toLowerCase()
        title = path.replace("\\s+".toRegex(), " ")
        this.library!!.books()[_id] = this
    }
}
