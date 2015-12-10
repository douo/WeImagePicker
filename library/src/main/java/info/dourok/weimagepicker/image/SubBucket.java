package info.dourok.weimagepicker.image;


import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by John on 2015/11/16.
 */
public class SubBucket extends Bucket {
    private final static String SELECTION = MediaStore.Images.Media.BUCKET_ID + "=?";
    private final static String MIME_TYPE_SELECTION = MediaStore.Images.Media.BUCKET_ID + "=? And " + MediaStore.Images.Media.MIME_TYPE + "=?";
    private final static String KEY_BUCKET = "info.dourok.weimagepicker.image.SubBucket";
    private final static String KEY_NAME = KEY_BUCKET + ".NAME";
    private final static String KEY_COUNT = KEY_BUCKET + ".COUNT";
    private final static String KEY_FIRST_IMAGE_ID = KEY_BUCKET + ".FIRST_IMAGE_ID";
    private final static String KEY_ID = KEY_BUCKET + "._ID";
    private final static String KEY_MIME_TYPE = KEY_BUCKET + ";MIME_TYPE";

    private String name;
    private long id;
    private long firstImageId;
    private int count;
    private String mimeType;

    SubBucket() {
    }

    public SubBucket(long id, String name, long firstImageId, String mimeType) {
        this.id = id;
        this.name = name;
        this.firstImageId = firstImageId;
        count = 1;
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    void count() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public long getFirstImageId() {
        return firstImageId;
    }

    public CursorLoader createLoader(Context context) {
        if (ImageContentManager.isExplicitMimeType(mimeType)) {
            return new CursorLoader(context, ImageContentManager.URI, ImageContentManager.PROJECTION, MIME_TYPE_SELECTION, new String[]{Long.toString(id), mimeType}, ImageContentManager.ORDER);
        } else {
            return new CursorLoader(context, ImageContentManager.URI, ImageContentManager.PROJECTION, SELECTION, new String[]{Long.toString(id)}, ImageContentManager.ORDER);
        }
    }

    @Override
    public void putIntoBundle(Bundle bundle) {
        bundle.putLong(KEY_ID, getId());
        bundle.putLong(KEY_FIRST_IMAGE_ID, getFirstImageId());
        bundle.putString(KEY_NAME, getName());
        bundle.putInt(KEY_COUNT, getCount());
        bundle.putString(KEY_MIME_TYPE, getMimeType());
    }

    @Override
    public void readFromBundle(Bundle bundle) {
        id = bundle.getLong(KEY_ID);
        firstImageId = bundle.getLong(KEY_FIRST_IMAGE_ID);
        name = bundle.getString(KEY_NAME);
        count = bundle.getInt(KEY_COUNT);
        mimeType = bundle.getString(KEY_MIME_TYPE);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof SubBucket && ((SubBucket) o).id == id;
    }

    @Override
    public String toString() {
        return name;
    }
}
