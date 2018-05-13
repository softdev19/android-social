package com.intrix.social.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class ImageUtils {

    public static final String LOG_TAG = ImageUtils.class.getSimpleName();

    public static void setBitmapHeader(AppCompatActivity context, ImageView topView, int res) {
        int headerHeight = context.getResources().getDimensionPixelSize(R.dimen.image_top_height);
        setBitmapHeader(context, topView, res, headerHeight);
    }

    public static void setBitmapHeader(AppCompatActivity context, ImageView topView, int res, int headerHeight) {
        ActionBar toolbar = context.getSupportActionBar();

        if (toolbar == null) {
            return;
        }

        int dashHeight = context.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        int width = getScreenWidth(context);

        Bitmap headerFullBitmap = BitmapFactory.decodeResource(context.getResources(), res);
        headerFullBitmap = Bitmap.createScaledBitmap(headerFullBitmap, width,
                headerHeight + dashHeight,
                false
        );

        Bitmap[] splitBitmaps = splitBitmapAtHeight(headerFullBitmap,
                dashHeight + headerHeight,
                dashHeight
        );

        toolbar.setBackgroundDrawable(new BitmapDrawable(context.getResources(), splitBitmaps[0]));
        topView.setImageBitmap(splitBitmaps[1]);

        headerFullBitmap.recycle();
    }

    public static void setBitmapHeader(AppCompatActivity a, ImageView imageView, String url) {
        setBitmapHeader(a, imageView, url, (int) a.getResources().getDimension(R.dimen.image_top_height));
    }

    public static void setBitmapHeader(final AppCompatActivity context, final ImageView imageView, final String url, final int headerHeight) {
        final ActionBar toolbar = context.getSupportActionBar();
        if (toolbar == null) {
            return;
        }
        final int dashHeight = context.getResources().getDimensionPixelSize(R.dimen.toolbar_height);

        final int width = getScreenWidth(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Glide.with(context).load(url).asBitmap()
                            .into(width, dashHeight + headerHeight)
                            .get();
                    bitmap = Bitmap.createScaledBitmap(bitmap, width,
                            headerHeight + dashHeight,
                            false
                    );

                    final Bitmap[] splitBitmaps = splitBitmapAtHeight(bitmap,
                            dashHeight + headerHeight,
                            dashHeight
                    );

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Drawable d = new BitmapDrawable(context.getResources(), splitBitmaps[0]);
                            int color = Color.parseColor("#6e000000");
                            d.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                            toolbar.setBackgroundDrawable(d);
                            imageView.setImageBitmap(splitBitmaps[1]);
                            imageView.setColorFilter(color);
                        }
                    });
                    bitmap.recycle();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }

            }
        }).start();
    }

    public static Bitmap[] splitBitmapAtHeight(Bitmap picture, int heightFull, int height) {
        Log.d(LOG_TAG, "height: " + height + "; heightFull: " + heightFull);
        height = height * picture.getHeight() / heightFull;
        Bitmap[] imgs = new Bitmap[2];
        Log.d(LOG_TAG, "height: " + height);
        imgs[0] = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), height);
        imgs[1] = Bitmap.createBitmap(picture, 0, height, picture.getWidth(), picture.getHeight() - height);
        return imgs;
    }

    public static int getScreenWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
