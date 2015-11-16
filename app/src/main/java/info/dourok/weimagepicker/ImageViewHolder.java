package info.dourok.weimagepicker;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * Created by John on 2015/11/16.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    CheckBox selector;
    ImageView image;

    public ImageViewHolder(View itemView) {
        super(itemView);
        selector = (CheckBox) itemView.findViewById(R.id.selector);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    public void populate(Bitmap bitmap, boolean select) {
        image.setImageBitmap(bitmap);
        selector.setChecked(select);
        selector.setSelected(select);
    }

}
