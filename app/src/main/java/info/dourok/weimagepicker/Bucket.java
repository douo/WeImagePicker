package info.dourok.weimagepicker;


import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by John on 2015/11/16.
 */
public class Bucket {
    private final static String SELECTION = MediaStore.Images.Media.BUCKET_ID + "=?";

    private String mName;
    private long mId;
    private long firstImageMiniThumbMagic;
    private int count;

    public Bucket(long id, String name, long miniThumb) {
        this.mId = id;
        this.mName = name;
        this.firstImageMiniThumbMagic = miniThumb;
        count = 1;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }

    public long getFirstImageMiniThumbMagic() {
        return firstImageMiniThumbMagic;
    }

    void count() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public CursorLoader createLoader(Context context) {
        return new CursorLoader(context, ImageContentManager.URI, ImageContentManager.PROJECTION, SELECTION, new String[]{Long.toString(mId)}, ImageContentManager.ORDER);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Bucket && ((Bucket) o).mId == mId;
    }
}
