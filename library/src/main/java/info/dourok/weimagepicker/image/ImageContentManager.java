package info.dourok.weimagepicker.image;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import info.dourok.weimagepicker.R;

/**
 * Created by John on 2015/11/16.
 */
public class ImageContentManager implements LoaderManager.LoaderCallbacks<Cursor> {
    public final static Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public final static String[] PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DISPLAY_NAME,
    };
    public final static String ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

    private final static String[] BUCKET_PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
    };
    private final static String BUCKET_ORDER = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private final static String MIME_TYPE_SELECTION = MediaStore.Images.Media.MIME_TYPE + "=?";
    private final static int BUCKET_LOADER_ID = 1234;
    public static final String TAG = "ImageContentManager";
    private FragmentActivity mContext;
    private List<Bucket> mBucketList;
    private Bucket mDeviceImageBucket;
    private PrepareCallback mPrepareCallback;
    private String mimeType;

    public interface PrepareCallback {
        void onPrepared();
    }

    public ImageContentManager(FragmentActivity context, String mimeType) {
        this.mContext = context;
        this.mimeType = mimeType;
    }

    public void prepare(PrepareCallback callback) {
        mPrepareCallback = callback;
        mContext.getSupportLoaderManager().initLoader(BUCKET_LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"onCreateLoader:"+id);
        if (BUCKET_LOADER_ID == id) {
            if (isExplicitMimeType(mimeType)) {
                return new CursorLoader(mContext, URI,
                        BUCKET_PROJECTION,
                        MIME_TYPE_SELECTION,
                        new String[]{mimeType},
                        ImageContentManager.BUCKET_ORDER);
            } else {
                return new CursorLoader(mContext, URI,
                        BUCKET_PROJECTION,
                        null,
                        null,
                        ImageContentManager.BUCKET_ORDER);
            }
        } else {
            throw new IllegalArgumentException("Unknown loader id:" + id);
        }
    }

    /**
     * initial all bucket
     * TODO http://stackoverflow.com/questions/6744803/sqlite-count-group-and-order-by-count
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        d("onLoadFinished");
        mBucketList = new ArrayList<>();
        long firstImageId = 0;
        if (data.moveToFirst()) {
            int bIdColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bNameColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int idColumn = data.getColumnIndex(MediaStore.Images.Media._ID);
            firstImageId = data.getLong(idColumn);
            SubBucket preBucket = null;
            do {
//                DatabaseUtils.dumpCurrentRow(data, System.out);
                long bId = data.getLong(bIdColumn);
                if (preBucket == null || bId != preBucket.getId()) {
                    preBucket = new SubBucket(bId, data.getString(bNameColumn), data.getLong(idColumn), mimeType);
                    mBucketList.add(preBucket);
                } else {
                    preBucket.count();
                }
            } while (data.moveToNext());
        }
        mDeviceImageBucket = new DeviceImageBucket(mContext.getString(R.string.weimagepicker__name_all_image), data.getCount(), firstImageId, mimeType);
        mPrepareCallback.onPrepared();
    }

    public Bucket getDeviceImageBucket() {
        return mDeviceImageBucket;
    }

    public List<Bucket> getAllBucketList() {
        List<Bucket> list = getSubBucketList();
        list.add(0, getDeviceImageBucket());
        return list;
    }

    public List<Bucket> getSubBucketList() {
        return new ArrayList<>(mBucketList);
    }

    /**
     * TODO
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        d("onLoaderReset");
        if (mBucketList != null) {
            mBucketList.clear();
        }
    }

    private void d(String msg) {
        Log.d(ImageContentManager.class.getName(), msg);
    }

    public static boolean isExplicitMimeType(String mimeType) {
        return (mimeType != null && !"image/*".equals(mimeType));
    }

}
