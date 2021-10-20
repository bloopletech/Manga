package net.bloople.manga;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MatchWidthTransformation extends BitmapTransformation {
    private static final String ID = "net.bloople.manga.MatchWidthTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(StandardCharsets.UTF_8);

    @Override
    public Bitmap transform(@NonNull BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int targetWidth = outWidth;
        int targetHeight = toTransform.getHeight();

        return TransformationUtils.fitCenter(pool, toTransform, targetWidth, targetHeight);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MatchWidthTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}