package info.dourok.weimagepicker;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by John on 2015/11/16.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    ImageButton selector;
    ImageView image;

    public ImageViewHolder(View itemView) {
        super(itemView);
        selector = (ImageButton) itemView.findViewById(R.id.selector);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    public void populate(Bitmap bitmap, boolean select) {
        //System.out.println(getAdapterPosition() + ":" + bitmap.getWidth() + "/" + bitmap.getHeight());
        image.setImageBitmap(bitmap);
        selector.setSelected(select);
    }

}
