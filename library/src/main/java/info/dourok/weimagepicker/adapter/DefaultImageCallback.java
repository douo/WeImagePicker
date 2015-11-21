package info.dourok.weimagepicker.adapter;

import android.content.Context;

import info.dourok.weimagepicker.ImagePreviewActivity;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.SelectedBucket;

/**
 * Created by John on 2015/11/21.
 */
public class DefaultImageCallback implements OnImageCallback {
    Context mContext;
    SelectedBucket mSelectedBucket;
    Bucket currentBucket;

    public DefaultImageCallback(Context context, SelectedBucket selectedBucket) {
        mContext = context;
        mSelectedBucket = selectedBucket;
        currentBucket = selectedBucket;
    }

    public void setCurrentBucket(Bucket currentBucket) {
        this.currentBucket = currentBucket;
    }

    @Override
    public void onTakePicture() {
        System.out.println("onTakePicture");
    }

    @Override
    public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
        boolean selected = mSelectedBucket.toggle(imageId);
        holder.setSelected(selected);
    }

    @Override
    public void onImageClick(ImageViewHolder holder, long imageId, int position) {
        mContext.startActivity(ImagePreviewActivity.createIntentForBucket(mContext, currentBucket, mSelectedBucket, position));
    }

    @Override
    public boolean isSelected(ImageViewHolder holder, long imageId) {
        return mSelectedBucket.isSelected(imageId);
    }
}
