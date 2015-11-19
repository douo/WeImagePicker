package info.dourok.weimagepicker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import info.dourok.weimagepicker.R;
import info.dourok.weimagepicker.image.SelectedBucket;

/**
 * Created by John on 2015/11/16.
 */
public class ImageAdapter extends CursorRecyclerViewAdapter<ImageViewHolder> {
    private static final int VIEW_TYPE_CAMERA = 0x1;
    private static final int VIEW_TYPE_IMAGE = 0x2;
    private boolean supportCamera;
    private SelectedBucket selectedBucket;
    private OnImageSelectListener selectListener;

    public ImageAdapter(Context context, Cursor cursor, SelectedBucket selectedBucket, boolean supprotCamera, OnImageSelectListener listener) {
        super(context, cursor);
        this.supportCamera = supprotCamera;
        this.selectedBucket = selectedBucket;
        this.selectListener = listener;
    }


    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, Cursor cursor) {
        viewHolder.populate(mContext, mContext.getContentResolver(), cursor, selectedBucket);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == VIEW_TYPE_CAMERA) {
            return new ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false), selectedBucket, selectListener);
        } else {
            return new ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false), selectedBucket, selectListener);
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
