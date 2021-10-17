package net.bloople.manga

import okhttp3.Credentials.basic
import android.os.Parcelable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlin.Throws
import android.os.Parcel
import android.os.Parcelable.Creator
import java.io.IOException
import java.net.URL
import java.net.URLConnection

class MangosUrl : Parcelable {
    private var url: String?
    private var username: String?
    private var password: String?

    constructor(url: String?, username: String?, password: String?) {
        this.url = url
        this.username = username
        this.password = password
    }

    fun withAppendedPath(pathSegment: String): MangosUrl {
        return MangosUrl(url + pathSegment, username, password)
    }

    fun toGlideUrl(): GlideUrl {
        if(username != null && password != null) {
            val credential = basic(username!!, password!!)
            return GlideUrl(url, LazyHeaders.Builder().addHeader("Authorization", credential).build())
        }
        return GlideUrl(url)
    }

    @Throws(IOException::class)
    fun toUrlConnection(): URLConnection {
        if(username != null && password != null) {
            val credential = basic(username!!, password!!)
            val connection = URL(url).openConnection()
            connection.addRequestProperty("Authorization", credential)
            return connection
        }
        return URL(url).openConnection()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(username)
        dest.writeString(password)
    }

    fun readFromParcel(source: Parcel) {
        url = source.readString()
        username = source.readString()
        password = source.readString()
    }

    protected constructor(`in`: Parcel) {
        url = `in`.readString()
        username = `in`.readString()
        password = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Creator<MangosUrl> = object : Creator<MangosUrl> {
            override fun createFromParcel(source: Parcel): MangosUrl {
                return MangosUrl(source)
            }

            override fun newArray(size: Int): Array<MangosUrl?> {
                return arrayOfNulls(size)
            }
        }
    }
}