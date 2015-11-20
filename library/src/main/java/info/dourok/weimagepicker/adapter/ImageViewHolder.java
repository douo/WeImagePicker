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

import info.dourok.lruimage.LruImage;
import info.dourok.lruimage.LruImageException;
import info.dourok.lruimage.LruImageTask;
import info.dourok.weimagepicker.R;
import info.dourok.weimagepicker.image.SelectedBucket;

/**
 * Created by John on 2015/11/16.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    ImageButton selector;
    ImageView image;
    View mask;
    LruImageTask currentTask;
    SelectedBucket bucket;
    private OnImageSelectListener selectListener;

    public ImageViewHolder(View itemView, boolean clickable, SelectedBucket bucket, OnImageSelectListener listener) {
        super(itemView);
        this.bucket = bucket;
        selectListener = listener;
        selector = (ImageButton) itemView.findViewById(R.id.selector);
        image = (ImageView) itemView.findViewById(R.id.image);
        mask = itemView.findViewById(R.id.mask);
        View.OnClickListener onSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) selector.getTag();
                boolean selected = ImageViewHolder.this.bucket.toggle(id);
                selector.setSelected(selected);
                if (selected) {
                    mask.setVisibility(View.VISIBLE);
                } else {
                    mask.setVisibility(View.INVISIBLE);
                }
                selectListener.onImageSelected(id, getAdapterPosition());
            }
        };
        selector.setOnClickListener(onSelect);
        if (clickable) {
            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = (long) selector.getTag();
                    selectListener.onImageClick(id, getAdapterPosition());
                }
            };
            itemView.setOnClickListener(onClick);
        } else {
            itemView.setOnClickListener(onSelect);
        }
    }


    public void populate(Context context, ContentResolver resolver, Cursor cursor, SelectedBucket bucket) {
        if (currentTask != null) {
            currentTask.cancel();
        }
        long origId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        image.setImageResource(R.drawable.weimagepicker__empty_image);
        LruThumbnail thumbnail = new LruThumbnail(resolver, origId);
        currentTask = new LruImageTask(context, thumbnail, new LruImageTask.OnCompleteListener() {
            @Override
            public void onSuccess(LruImage lruImage, Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(LruImage lruImage, LruImageException e) {
                d(getAdapterPosition() + " failure:" + e.getMessage());
            }

            @Override
            public void cancel() {
                d("cancel:" + getAdapterPosition());
            }
        }).execute();
        boolean selected = bucket.isSelected(origId);
        itemView.setTag(origId);
        selector.setTag(origId);
        selector.setSelected(selected);
        if (selected) {
            mask.setVisibility(View.VISIBLE);
        } else {
            mask.setVisibility(View.INVISIBLE);
        }
    }


    private void d(String s) {
        Log.d("ImageViewHolder", s);
    }

}
