package info.dourok.weimagepicker;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import info.dourok.weimagepicker.image.AllImageBucket;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.ImageContentManager;
import info.dourok.weimagepicker.image.SelectedBucket;
import info.dourok.weimagepicker.image.SubBucket;

public class ImagePreviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Bucket mBucket;
    SelectedBucket mSelectedBucket;
    ViewPager mPager;

    public static final String KEY_BUCKET_TYPE = "KEY_BUCKET_TYPE";
    public static final String KEY_POSITION = "KEY_POSITION";

    public static final int BUCKET_TYPE_SUB = 0x1;
    public static final int BUCKET_TYPE_SELECTED = BUCKET_TYPE_SUB + 1;
    public static final int BUCKET_TYPE_ALL = BUCKET_TYPE_SELECTED + 1;

    private final static int LOADER_ID = 0x12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weimagepicker__activity_image_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle((position + 1) + "/" + mBucket.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSelectedBucket = Bucket.fromIntent(getIntent(), SelectedBucket.class);
        int bucketType = getIntent().getIntExtra(KEY_BUCKET_TYPE, BUCKET_TYPE_SELECTED);
        switch (bucketType) {
            case BUCKET_TYPE_ALL:
                mBucket = Bucket.fromIntent(getIntent(), AllImageBucket.class);
                break;
            case BUCKET_TYPE_SELECTED:
                mBucket = Bucket.fromIntent(getIntent(), SelectedBucket.class);
                break;
            case BUCKET_TYPE_SUB:
                mBucket = Bucket.fromIntent(getIntent(), SubBucket.class);
                break;
        }
        d(mBucket.getName());
        setTitle((getIntent().getIntExtra(KEY_POSITION, 0) + 1) + "/" + mBucket.getCount());
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mBucket.createLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ImagePagerAdapter adapter = new ImagePagerAdapter(data);
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(getIntent().getIntExtra(KEY_POSITION, 0));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private class ImagePagerAdapter extends PagerAdapter {
        Cursor mCursor;

        public ImagePagerAdapter(Cursor cursor) {
            this.mCursor = cursor;
        }

        @Override
        public int getCount() {
            return mBucket.getCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            d("instantiateItem:" + position);
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) getLayoutInflater().
                    inflate(R.layout.weimagepicker__item_pager_image, container, false);
            mCursor.moveToPosition(position);
            DatabaseUtils.dumpCurrentRow(mCursor, System.out);
            long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
            imageView.setTag(id);
            imageView.setImage(ImageSource.uri(ContentUris.withAppendedId(ImageContentManager.URI, id)));
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            d("isViewFromObject:" + object);
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            d("destroyItem:" + position + " " + object);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void d(String s) {
        Log.d("ImagePreviewActivity", s);
    }

    public static Intent createIntentForSubBucket(Context context, SubBucket bucket, SelectedBucket selectedBucket, int position) {
        Intent i = new Intent(context, ImagePreviewActivity.class);
        i.putExtra(KEY_BUCKET_TYPE, BUCKET_TYPE_SUB);
        i.putExtra(KEY_POSITION, position);
        bucket.putIntoIntent(i);
        selectedBucket.putIntoIntent(i);
        return i;
    }

    public static Intent createIntentForSelectedBucket(Context context, SelectedBucket selectedBucket, int position) {
        Intent i = new Intent(context, ImagePreviewActivity.class);
        i.putExtra(KEY_BUCKET_TYPE, BUCKET_TYPE_SELECTED);
        i.putExtra(KEY_POSITION, position);
        selectedBucket.putIntoIntent(i);
        return i;
    }

    public static Intent createIntentForAllImageBucket(Context context, AllImageBucket bucket, SelectedBucket selectedBucket, int position) {
        Intent i = new Intent(context, ImagePreviewActivity.class);
        i.putExtra(KEY_BUCKET_TYPE, BUCKET_TYPE_ALL);
        i.putExtra(KEY_POSITION, position);
        bucket.putIntoIntent(i);
        selectedBucket.putIntoIntent(i);
        return i;
    }

    public static Intent createIntentForBucket(Context context, Bucket bucket, SelectedBucket selectedBucket, int position) {
        if (bucket instanceof AllImageBucket) {
            return createIntentForAllImageBucket(context, (AllImageBucket) bucket, selectedBucket, position);
        } else if (bucket instanceof SubBucket) {
            return createIntentForSubBucket(context, (SubBucket) bucket, selectedBucket, position);
        } else if (bucket instanceof SelectedBucket) {
            return createIntentForSelectedBucket(context, selectedBucket, position);
        } else {
            throw new IllegalArgumentException("Unknown bucket type.:" + bucket.getName());
        }
    }
}
