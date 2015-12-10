package info.dourok.weimagepicker;

import android.content.Context;
import android.content.Intent;

/**
 * Created by John on 2015/12/9.
 */
public class PickerBuilder {
    private Context mContext;
    private Intent intent;

    public PickerBuilder(Context context) {
        this.mContext = context;
        intent = new Intent(context, ImagePickerActivity.class);
    }

    public PickerBuilder setShowCameraButton(boolean show) {
        intent.putExtra(ImagePickerActivity.EXTRA_SHOW_CAMERA_BUTTON, show);
        return this;
    }

    public PickerBuilder setSelectedImageLimit(int limit) {
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTED_IMAGE_LIMIT, limit);
        return this;
    }

    public void start() {
        mContext.startActivity(intent);
    }

    public Intent createIntent() {
        return intent;
    }


}
