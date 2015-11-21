package info.dourok.weimagepicker.adapter;

/**
 * Created by John on 2015/11/18.
 */
public interface OnImageCallback {
    void onTakePicture();

    void onImageSelect(ImageViewHolder holder, long imageId, int position);

    void onImageClick(ImageViewHolder holder, long imageId, int position);

    boolean isSelected(ImageViewHolder holder, long imageId);

}
