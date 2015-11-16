package info.dourok.weimagepicker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by John on 2015/11/16.
 */
public class ImageAdapter extends CursorRecyclerViewAdapter<ImageViewHolder> {
    private static final int VIEW_TYPE_CAMERA = 0x1;
    private static final int VIEW_TYPE_IMAGE = 0x2;
    private boolean supportCamera;

    public ImageAdapter(Context context, Cursor cursor, boolean supprotCamera) {
        super(context, cursor);
        this.supportCamera = supprotCamera;
    }


    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, Cursor cursor) {
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), cursor.getColumnIndex(MediaStore.Images.Media._ID), MediaStore.Images.Thumbnails.MINI_KIND, null);
        viewHolder.populate(bitmap, false);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAMERA) {
            return new ImageViewHolder(View.inflate(mContext, R.layout.item_image, parent));
        } else {
            return new ImageViewHolder(View.inflate(mContext, R.layout.item_image, parent));
        }
    }


    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, int position) {
        if (supportCamera) {
            if (position == 0) {

            } else {
                position--;
                super.onBindViewHolder(viewHolder, position);
            }
        } else {
            super.onBindViewHolder(viewHolder, position);
        }


    }

    @Override
    public int getItemCount() {
        if (supportCamera) {
            return super.getItemCount() + 1;
        } else {
            return super.getItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (supportCamera && position == 0) {
            return VIEW_TYPE_CAMERA;
        } else {
            return VIEW_TYPE_IMAGE;
        }

    }
}
