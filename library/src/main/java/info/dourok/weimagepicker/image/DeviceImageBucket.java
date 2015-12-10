package info.dourok.weimagepicker.image;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * 处理设备上的所有图片
 */
public class DeviceImageBucket extends Bucket {

    private final static String KEY_BUCKET = "info.dourok.weimagepicker.image.DeviceImageBucket";
    private final static String KEY_NAME = KEY_BUCKET + ".NAME";
    private final static String KEY_COUNT = KEY_BUCKET + ".COUNT";
    private final static String KEY_FIRST_IMAGE_ID = KEY_BUCKET + ".FIRST_IMAGE_ID";
    private final static String KEY_MIME_TYPE = KEY_BUCKET + ";MIME_TYPE";

    private final static String MIME_TYPE_SELECTION = MediaStore.Images.Media.MIME_TYPE + "=?";

    private String name;
    private int count;
    private long firstImageId;
    private String mimeType;

    DeviceImageBucket() {
    }

    public DeviceImageBucket(String name, int count, long firstImageId, String mimeType) {
        this.name = name;
        this.count = count;
        this.firstImageId = firstImageId;
        this.mimeType = mimeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public long getFirstImageId() {
        return firstImageId;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public CursorLoader createLoader(Context context) {
        if (ImageContentManager.isExplicitMimeType(mimeType)) {
            return new CursorLoader(context, ImageContentManager.URI,
                    ImageContentManager.PROJECTION,
                    MIME_TYPE_SELECTION,
                    new String[]{mimeType},
                    ImageContentManager.ORDER);
        } else {
            return new CursorLoader(context, ImageContentManager.URI,
                    ImageContentManager.PROJECTION,
                    null,
                    null,
                    ImageContentManager.ORDER);
        }
    }

    @Override
    public void putIntoBundle(Bundle bundle) {
        bundle.putLong(KEY_FIRST_IMAGE_ID, getFirstImageId());
        bundle.putString(KEY_NAME, getName());
        bundle.putInt(KEY_COUNT, getCount());
        bundle.putString(KEY_MIME_TYPE, getMimeType());
    }

    @Override
    public void readFromBundle(Bundle bundle) {
        firstImageId = bundle.getLong(KEY_FIRST_IMAGE_ID);
        name = bundle.getString(KEY_NAME);
        count = bundle.getInt(KEY_COUNT);
        mimeType = bundle.getString(KEY_MIME_TYPE);
    }
}