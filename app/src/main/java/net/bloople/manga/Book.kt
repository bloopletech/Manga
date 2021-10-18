package net.bloople.manga

import kotlinx.serialization.SerialName
import java.util.ArrayList

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Book(
    val path: String,
    @SerialName("pagePaths")
    val pagePathsDeflated: String,
    val pages: Int,
    val publishedOn: Int,
    val key: String,
    val tags: List<String>
) {
    @Transient
    lateinit var library: Library
        private set
    val title: String by lazy { path.replace("\\s+".toRegex(), " ") }
    val normalisedTitle: String by lazy { path.replace("[^A-Za-z0-9]+".toRegex(), "").toLowerCase() }
    val id: Long by lazy { key.substring(0, 15).toLong(16) } //Using substring of key would be dangerous for large N
    val pagePaths: ArrayList<String> by lazy { PagesInflater(pagePathsDeflated).inflate() }

    fun thumbnailUrl(): MangosUrl {
        return library.mangos().withAppendedPath("/img/thumbnails/$key.jpg")
    }

    fun pageUrl(index: Int): MangosUrl {
        return library.rootUrl().withAppendedPath("/" + path + "/" + pagePaths[index])
    }

    fun inflate(library: Library) {
        this.library = library
        library.books[id] = this
    }
}
