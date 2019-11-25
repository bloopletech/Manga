package net.bloople.manga;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Surface;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

public class AutoRotateTransformation extends BitmapTransformation {
    private Activity activity;
    private ImageView view;

    AutoRotateTransformation(Activity activity, ImageView view) {
        super(activity);
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if(getDisplayLandscape()) {
            view.setAdjustViewBounds(true);
            return toTransform;
        }

        boolean bitmapLandscape = toTransform.getWidth() > toTransform.getHeight();
        boolean targetLandscape = outWidth > outHeight;

        if(bitmapLandscape == targetLandscape) {
            view.setAdjustViewBounds(true);
            return toTransform;
        }

        view.setAdjustViewBounds(false);

        Matrix matrix = new Matrix();
        matrix.postRotate(-90f);
        Bitmap rotated = Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        return TransformationUtils.fitCenter(rotated, pool, outWidth, outHeight);
    }

    @Override
    public String getId() {
        return "auto-rotate";
    }

    private boolean getDisplayLandscape() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;
    }
}