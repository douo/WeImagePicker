package info.dourok.weimagepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

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
    public final static String ORDER = MediaStore.Images.Media.DATE_ADDED;

    private final static String[] BUCKET_PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
    };
    private final static String BUCKET_ORDER = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private final static int BUCKET_LOADER_ID = 1234;
    private FragmentActivity mContext;
    private List<Bucket> mBucketList;
    private Bucket mAllImageBucket;
    private PrepareCallback mPrepareCallback;


    public interface PrepareCallback {
        void onPrepared();
    }

    public ImageContentManager(FragmentActivity context) {
        this.mContext = context;
    }

    public void prepare(PrepareCallback callback) {
        mPrepareCallback = callback;
        mContext.getSupportLoaderManager().initLoader(BUCKET_LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (BUCKET_LOADER_ID == id) {
            return new CursorLoader(mContext, URI,
                    BUCKET_PROJECTION,
                    null,
                    null,
                    ImageContentManager.BUCKET_ORDER);
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

        mBucketList = new ArrayList<>();
        long firstImageId = 0;
        if (data.moveToFirst()) {
            int bIdColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bNameColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int idColumn = data.getColumnIndex(MediaStore.Images.Media._ID);
            firstImageId = data.getLong(idColumn);
            SubBucket preBucket = null;
            do {
                //DatabaseUtils.dumpCurrentRow(data, System.out);
                long bId = data.getLong(bIdColumn);
                if (preBucket == null || bId != preBucket.getId()) {
                    preBucket = new SubBucket(bId, data.getString(bNameColumn), data.getLong(idColumn));
                    mBucketList.add(preBucket);
                } else {
                    preBucket.count();
                }
            } while (data.moveToNext());
        }
        mAllImageBucket = new AllImageBucket("All Images", data.getCount(), firstImageId);
        data.close();
        mPrepareCallback.onPrepared();
    }

    public Bucket getAllImageBucket() {
        return mAllImageBucket;
    }

    public List<Bucket> getAllBucketList() {
        List<Bucket> list = getSubBucketList();
        list.add(0, getAllImageBucket());
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
        if (mBucketList != null) {
            mBucketList.clear();
        }
    }

    private static class AllImageBucket implements Bucket {
        private String name;
        private int count;
        private long firstImageId;

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
            return new CursorLoader(context, URI,
                    PROJECTION,
                    null,
                    null,
                    ImageContentManager.ORDER);
        }
    }
}
