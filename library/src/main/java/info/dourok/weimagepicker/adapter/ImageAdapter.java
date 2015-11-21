package info.dourok.weimagepicker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import info.dourok.weimagepicker.R;

/**
 * Created by John on 2015/11/16.
 */
public class ImageAdapter extends CursorRecyclerViewAdapter<ImageViewHolder> implements OnImageCallback {
    private static final int VIEW_TYPE_CAMERA = 0x1;
    private static final int VIEW_TYPE_IMAGE = 0x2;
    private boolean supportCamera;
    private OnImageCallback callback;

    public ImageAdapter(Context context, Cursor cursor, boolean supportCamera, OnImageCallback listener) {
        super(context, cursor);
        this.supportCamera = supportCamera;
        this.callback = listener;
    }


    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, Cursor cursor) {
        viewHolder.populate(mContext, mContext.getContentResolver(), cursor);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == VIEW_TYPE_CAMERA) {
            return new ImageViewHolder(inflater.inflate(R.layout.weimagepicker__item_image, parent, false), this);
        } else {
            return new ImageViewHolder(inflater.inflate(R.layout.weimagepicker__item_image, parent, false), this);
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

    @Override
    public void onTakePicture() {
        callback.onTakePicture();
    }

    @Override
    public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
        if (supportCamera) {
            if (position == 0) {
                callback.onTakePicture();
                return;
            } else {
                position--;
            }
        }
        callback.onImageSelect(holder, imageId, position);
    }

    @Override
    public void onImageClick(ImageViewHolder holder, long imageId, int position) {
        if (supportCamera) {
            if (position == 0) {
                callback.onTakePicture();
                return;
            } else {
                position--;
            }
        }
        callback.onImageClick(holder, imageId, position);
    }

    @Override
    public boolean isSelected(ImageViewHolder holder, long imageId) {
        return callback.isSelected(holder, imageId);
    }
}
