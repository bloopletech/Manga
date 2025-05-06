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
    val title: String by lazy { path.replace("\\s+".toRegex(), " ") }
    val normalisedTitle: String by lazy { path.replace("[^A-Za-z0-9]+".toRegex(), "").lowercase() }
    val id: Long by lazy { key.substring(0, 15).toLong(16) } //Using substring of key would be dangerous for large N
    private val pagePaths: ArrayList<String> by lazy { inflatePagePaths(pagePathsDeflated) }
    val thumbnailUrl: MangosUrl by lazy { library.thumbnailsUrl / "$key.jpg" }

    fun pageUrl(index: Int): MangosUrl {
        return library.rootUrl / path / pagePaths[index]
    }
}
