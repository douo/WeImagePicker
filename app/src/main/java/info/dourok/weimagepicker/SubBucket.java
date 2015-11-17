package info.dourok.weimagepicker;


import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by John on 2015/11/16.
 */
public class SubBucket implements Bucket {
    private final static String SELECTION = MediaStore.Images.Media.BUCKET_ID + "=?";

    private String mName;
    private long mId;
    private long firstImageId;
    private int count;

    public SubBucket(long id, String name, long firstImageId) {
        this.mId = id;
        this.mName = name;
        this.firstImageId = firstImageId;
        count = 1;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }

    void count() {
        count++;
    }

    public int getCount() {
        return count;
    }

    @Override
    public long getFirstImageId() {
        return firstImageId;
    }

    public CursorLoader createLoader(Context context) {
        return new CursorLoader(context, ImageContentManager.URI, ImageContentManager.PROJECTION, SELECTION, new String[]{Long.toString(mId)}, ImageContentManager.ORDER);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof SubBucket && ((SubBucket) o).mId == mId;
    }

    @Override
    public String toString() {
        return mName;
    }
}
