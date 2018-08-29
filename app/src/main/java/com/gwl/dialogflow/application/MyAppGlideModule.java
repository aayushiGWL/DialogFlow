package com.gwl.dialogflow.application;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gwl.dialogflow.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public final class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
//        super.applyOptions(context, builder);
        // Glide default Bitmap Format is set to RGB_565 since it
        // consumed just 50% memory footprint compared to ARGB_8888.
        // Increase memory usage for quality with:

        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    public void GlideBasic(Context mContext, String url, ImageView ivImg) {
        Glide.with(mContext)
                .load("http://via.placeholder.com/300.png")
                .into(ivImg);
    }

    public void GlideResizingImage(Context mContext, String url, ImageView ivImg) {
        // resizes the image to 100x200 pixels but does not respect aspect ratio
        Glide.with(mContext)
                .load("http://via.placeholder.com/300.png")
                .apply(new RequestOptions().override(300, 200))
                .into(ivImg);
        //If you only want to resize one dimension, use Target.SIZE_ORIGINAL as a placeholder for the other dimension:
        Glide.with(mContext)
                .load("http://via.placeholder.com/300.png")
                .apply(new RequestOptions().override(100, Target.SIZE_ORIGINAL)) // resizes width to 100, preserves original height, does not respect aspect ratio
                .into(ivImg);
    }

    public void GlidePlaceHolderImage(Context mContext, String url, ImageView iv) {

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.img_not_found))
                .into(iv);
    }

    public void GlideCroppingImage(Context mContext, String url, ImageView iv) {
        //Calling centerCrop() scales the image so that it fills
        // the requested bounds of the ImageView and then crops the extra.
        // The ImageView will be filled completely,
        // but the entire image might not be displayed.
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().centerCrop())
                .into(iv);

        Glide.with(mContext)
                .load("http://via.placeholder.com/300.png")
                .apply(new RequestOptions().override(100, 200)
                        .centerCrop())// scale to fill the ImageView and crop any extra
                .into(iv);

    }

    public void GlideFitCenter(Context mContext, String url, ImageView iv) {
        //Calling fitCenter() scales the image so that both dimensions are equal
        // to or less than the requested bounds of the ImageView.
        // The image will be displayed completely, but might not fill the entire ImageView.

        Glide.with(mContext)
                .load("http://via.placeholder.com/300.png")
                .apply(new RequestOptions().override(100, 200)
                        .fitCenter())// scale to fit entire image within ImageView
                .into(iv);
    }

    public void GlideLoadingError(Context mContext, String url, ImageView iv) {
        //Loading Errors
        //If you experience errors loading images,
        // you can create a RequestListener<Drawable> and pass
        // it in via Glide.listener() to intercept errors:

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.img_not_found))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // log exception
                        Log.e("TAG", "Error loading image", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    public void GlideRoundedCorners(Context mContext, String url, ImageView iv) {

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; //crop margin, set to 0 for corners with no crop

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().transform(new RoundedCorners(radius)))
                .into(iv);
    }

    public static void GlideBasic(Context mContext, int image, ImageView ivProfilePic) {
        Glide.with(mContext)
                .load(image)
                .apply(new RequestOptions().transforms(new RoundedCornersTransformation(170, 10)))
                .into(ivProfilePic);
    }

    public static void GlideCircleCrop(Context mContext, int image, ImageView ivProfilePic) {

        Glide.with(mContext)
                .load(image)
                .apply(new RequestOptions().transforms(new CircleCrop()))
                .into(ivProfilePic);
    }

    public static void GlideBlurEffect(Context mContext, int image, ImageView ivProfilePic) {

        Glide.with(mContext)
                .load(image)
                .apply(new RequestOptions().transforms(new BlurTransformation()))
                .into(ivProfilePic);
    }

    public static void GlideMultipleEffect(Context mContext, int image, ImageView ivProfilePic) {

        Glide.with(mContext)
                .load(image)
                .apply(new RequestOptions().transforms(new MultiTransformation<Bitmap>(new CircleCrop(),new BlurTransformation(5))))
                .into(ivProfilePic);
    }

    public static void GlideImageResize(Context mContext, int image, final ImageView ivProfilePic) {

//         SimpleTarget target = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                // do something with the bitmap
//                // set it to an ImageView
//
//                ivProfilePic.setImageBitmap(resource);
//            }
//        };
//        Glide.with(mContext)
//                .load(image)
//                .into(target);
    }
}
