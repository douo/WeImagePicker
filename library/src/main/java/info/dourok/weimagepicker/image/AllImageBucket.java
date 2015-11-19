package info.dourok.weimagepicker.image;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

public class AllImageBucket extends Bucket {
    private String name;
    private int count;
    private long firstImageId;
    private final static String KEY_BUCKET = "info.dourok.weimagepicker.image.AllImageBucket";
    private final static String KEY_NAME = KEY_BUCKET + ";KEY_NAME";
    private final static String KEY_COUNT = KEY_BUCKET + ";KEY_COUNT";
    private final static String KEY_FIRST_IMAGE_ID = KEY_BUCKET + ";KEY_FIRST_IMAGE_ID";


    private AllImageBucket() {
    }

    public AllImageBucket(String name, int count, long firstImageId) {
        this.name = name;
        this.count = count;
        this.firstImageId = firstImageId;
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

    @Override
    public CursorLoader createLoader(Context context) {
        return new CursorLoader(context, ImageContentManager.URI,
                ImageContentManager.PROJECTION,
                null,
                null,
                ImageContentManager.ORDER);
    }

    @Override
    public void putIntoBundle(Bundle bundle) {
        bundle.putLong(KEY_FIRST_IMAGE_ID, getFirstImageId());
        bundle.putString(KEY_NAME, getName());
        bundle.putInt(KEY_COUNT, getCount());
    }

    @Override
    public void readFromBundle(Bundle bundle) {
        firstImageId = bundle.getLong(KEY_FIRST_IMAGE_ID);
        name = bundle.getString(KEY_NAME);
        count = bundle.getInt(KEY_COUNT);
    }
}