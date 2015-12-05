package info.dourok.weimagepicker;

import android.animation.Animator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    private static final String KEY_MODIFIED = "KEY_MODIFIED";
    Bucket mBucket;
    SelectedBucket mSelectedBucket;
    ViewPager mPager;
    View mBottomBar;
    View mSelectorText;
    View mSelector;
    public static final String KEY_BUCKET_TYPE = "KEY_BUCKET_TYPE";
    public static final String KEY_POSITION = "KEY_POSITION";

    public static final int BUCKET_TYPE_SUB = 0x1;
    public static final int BUCKET_TYPE_SELECTED = BUCKET_TYPE_SUB + 1;
    public static final int BUCKET_TYPE_ALL = BUCKET_TYPE_SELECTED + 1;

    private final static int LOADER_ID = 0x12;
    private boolean modified;
    View mDecorView;
    private boolean mDecorVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorVisible = true;
        setContentView(R.layout.weimagepicker__activity_image_preview);
        mDecorView = getWindow().getDecorView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBottomBar = findViewById(R.id.bottom_bar);
        mSelector = findViewById(R.id.selector);
        mSelectorText = findViewById(R.id.selector_text);
        View.OnClickListener selectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modified = true;
                boolean selected = mSelectedBucket.toggle(mAdapter.getId(mPager.getCurrentItem()));
                mSelector.setSelected(selected);
            }
        };
        mSelector.setOnClickListener(selectListener);
        mSelectorText.setOnClickListener(selectListener);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle((position + 1) + "/" + mBucket.getCount());
                checkSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (savedInstanceState != null) {
            modified = savedInstanceState.getBoolean(KEY_MODIFIED);
            mSelectedBucket = Bucket.fromBundle(savedInstanceState, SelectedBucket.class);
        } else {
            mSelectedBucket = Bucket.fromIntent(getIntent(), SelectedBucket.class);
        }
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
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_MODIFIED, modified);
        mSelectedBucket.putIntoBundle(outState);
        super.onSaveInstanceState(outState);
    }

    private void checkSelected(int position) {
        mSelector.setSelected(mSelectedBucket.isSelected(mAdapter.getId(position)));
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        getSupportActionBar().hide();
        mBottomBar.animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        mDecorVisible = false;
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getSupportActionBar().show();
        mDecorVisible = true;
        mBottomBar.setVisibility(View.VISIBLE);
        mBottomBar.setAlpha(1);
    }

    private void toggleSystemUI() {
        if (mDecorVisible) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    @Override
    public void finish() {
        if (modified) {
            Intent data = new Intent();
            mSelectedBucket.putIntoIntent(data);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    ImagePagerAdapter mAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mBucket.createLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new ImagePagerAdapter(data);
        mPager.setAdapter(mAdapter);
        int position = getIntent().getIntExtra(KEY_POSITION, 0);
        setTitle((position + 1) + "/" + mBucket.getCount());
        mPager.setCurrentItem(position);
        checkSelected(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private class ImagePagerAdapter extends PagerAdapter {
        long[] idsArray;
        Cursor mCursor;

        public ImagePagerAdapter(Cursor cursor) {
            this.mCursor = cursor;
            idsArray = new long[cursor.getCount()];
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
            //DatabaseUtils.dumpCurrentRow(mCursor, System.out);
            long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
            idsArray[position] = id;
            imageView.setImage(ImageSource.uri(ContentUris.withAppendedId(ImageContentManager.URI, id)));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSystemUI();
                }
            });
            container.addView(imageView);
            return imageView;
        }

        public long getId(int position) {
            return idsArray[position];
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
