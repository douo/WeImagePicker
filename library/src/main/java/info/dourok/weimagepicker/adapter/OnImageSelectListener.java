package info.dourok.weimagepicker.adapter;

/**
 * Created by John on 2015/11/18.
 */
public interface OnImageSelectListener {
    void onImageSelected(long imageId, int position);

    void onImageClick(long imageId, int position);
}
