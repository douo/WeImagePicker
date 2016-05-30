package info.dourok.weimagepicker.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import info.dourok.lruimage.LruImageException;
import info.dourok.lruimage.LruImageTask;
import info.dourok.lruimage.LruTaskBuilder;
import info.dourok.weimagepicker.R;

/**
 * Created by John on 2015/11/16.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    private final static ExecutorService THUMB_LOADER = Executors.newFixedThreadPool(4);
    ImageButton selector;
    ImageView image;
    View mask;
    LruImageTask currentTask;
    private OnImageCallback callback;

    public ImageViewHolder(View itemView, OnImageCallback listener) {
        super(itemView);
        callback = listener;
        selector = (ImageButton) itemView.findViewById(R.id.selector);
        image = (ImageView) itemView.findViewById(R.id.image);
        mask = itemView.findViewById(R.id.mask);
        View.OnClickListener onSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) selector.getTag();
                callback.onImageSelect(ImageViewHolder.this, id, getAdapterPosition());
            }
        };
        selector.setOnClickListener(onSelect);
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj = selector.getTag();
                if (obj == null) {
                    callback.onTakePicture();
                } else {
                    long id = (long) obj;
                    callback.onImageClick(ImageViewHolder.this, id, getAdapterPosition());
                }
            }
        };
        itemView.setOnClickListener(onClick);
    }

    public void setSelected(boolean selected) {
        selector.setSelected(selected);
        if (selected) {
            mask.setVisibility(View.VISIBLE);
        } else {
            mask.setVisibility(View.INVISIBLE);
        }
    }

    public void populateCamera() {
        selector.setVisibility(View.GONE);
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setBackgroundColor(0x33000000);
        image.setImageResource(R.drawable.weiimagepicker__ic_camera_large);
    }

    public void populate(Context context, ContentResolver resolver, Cursor cursor) {
        if (currentTask != null) {
            currentTask.cancel();
        }
        long origId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        image.setImageResource(R.drawable.weimagepicker__empty_image);
        LruThumbnail thumbnail = new LruThumbnail(resolver, origId);
        currentTask = new LruTaskBuilder(context)
                .setImageLoader(THUMB_LOADER)
                .success(new LruTaskBuilder.SuccessCallback() {
                    @Override
                    public void call(Bitmap bitmap) {
                        image.setImageBitmap(bitmap);
                    }
                })
                .failure(new LruTaskBuilder.FailureCallback() {
                    @Override
                    public void call(LruImageException e) {
                        d(getAdapterPosition() + " failure:" + e.getMessage());
                    }
                })
                .cancel(new LruTaskBuilder.CancelCallback() {
                    @Override
                    public void call() {
                        d("cancel:" + getAdapterPosition());
                    }
                })
                .execute(thumbnail);
        boolean selected = callback.isSelected(this, origId);
        itemView.setTag(origId);
        selector.setTag(origId);
        setSelected(selected);
    }

    private void d(String s) {
        Log.d("ImageViewHolder", s);
    }
}
