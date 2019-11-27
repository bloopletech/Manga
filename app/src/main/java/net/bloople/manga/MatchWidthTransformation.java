package net.bloople.manga;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

public class MatchWidthTransformation extends BitmapTransformation {
    MatchWidthTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int targetWidth = outWidth;
        int targetHeight = toTransform.getHeight();

        return TransformationUtils.fitCenter(toTransform, pool, targetWidth, targetHeight);
    }

    @Override
    public String getId() {
        return "auto-rotate";
    }
}