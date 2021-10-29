package net.bloople.manga

import android.net.Uri
import android.os.Parcelable
import kotlin.Throws
import android.os.Parcel
import android.os.Parcelable.Creator
import android.util.Base64
import android.widget.ImageView
import coil.load
import java.io.IOException
import java.net.URL
import java.net.URLConnection

open class MangosUrl(val url: String, private val credential: String? = null) : Parcelable {
    constructor(url: String, username: String?, password: String?) : this(
        url,
        if(username != null && password != null) {
            Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
        }
        else {
            null
        }
    )

    operator fun div(other: String): MangosUrl {
        return MangosUrl(url + "/" + Uri.encode(other), credential)
    }

//    fun with(builder: ImageRequest.Builder): ImageRequest.Builder {
//        if(credential != null) {
//            return builder.data(url).setHeader("Authorization", "Basic $credential")
//        }
//        return builder.data(url)
//    }

    fun load(imageView: ImageView) {
        imageView.load(url) {
            if(credential != null) {
                setHeader("Authorization", "Basic $credential")
            }
        }
    }

    @Throws(IOException::class)
    fun toUrlConnection(): URLConnection {
        if(credential != null) {
            return URL(url).openConnection().apply { addRequestProperty("Authorization", "Basic $credential") }
        }
        return URL(url).openConnection()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(credential)
    }

    protected constructor(input: Parcel) : this(input.readString()!!, input.readString())

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
