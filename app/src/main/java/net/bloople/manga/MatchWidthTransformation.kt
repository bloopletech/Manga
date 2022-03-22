package net.bloople.manga

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import android.graphics.Bitmap
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class MatchWidthTransformation : BitmapTransformation() {
    public override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val targetHeight = toTransform.height
        return TransformationUtils.fitCenter(pool, toTransform, outWidth, targetHeight)
    }

    override fun equals(other: Any?): Boolean {
        return other is MatchWidthTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val ID = "net.bloople.manga.MatchWidthTransformation"
        private val ID_BYTES = ID.toByteArray(StandardCharsets.UTF_8)
    }
}