package info.dourok.weimagepicker;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

import info.dourok.weimagepicker.adapter.BucketAdapter;
import info.dourok.weimagepicker.adapter.ImageAdapter;
import info.dourok.weimagepicker.adapter.ImageViewHolder;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.ImageContentManager;
import info.dourok.weimagepicker.image.SelectedBucket;

public class ImagePickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView mRecyclerView;
    Spinner mBucketSpinner;
    ImageContentManager mManager;
    List<Bucket> mBuckets;
    SelectedBucket mSelectedBucket;
    ImageAdapter mAdapter;
    DefaultImageCallback mImageCallback;

    public final static String KEY_SHOW_CAMERA_BUTTON = "info.dourok.weimagepicker:KEY_SHOW_CAMERA_BUTTON";

    protected boolean showCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedBucket = Bucket.fromBundle(savedInstanceState, SelectedBucket.class);
        } else {
            mSelectedBucket = new SelectedBucket();
        }
        showCameraButton = getIntent().getBooleanExtra(KEY_SHOW_CAMERA_BUTTON, false);
        setContentView(R.layout.weimagepicker__activity_image_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                            int space = getResources().getDimensionPixelSize(R.dimen.weimagepicker__grid_item_margin);

                                            @Override
                                            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                                outRect.right = space;
                                                outRect.left = space;
                                                outRect.bottom = space;
                                                outRect.top = space;
                                            }
                                        }
        );
        mRecyclerView.setLayoutManager(layoutManager);
        mImageCallback = new DefaultImageCallback(this, mSelectedBucket) {
            @Override
            public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
                super.onImageSelect(holder, imageId, position);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onCameraPhotoSaved(Uri uri) {
                mSelectedBucket.add(ContentUris.parseId(uri));
                done(mSelectedBucket.toUriArray());
            }
        };
        mAdapter = new ImageAdapter(this, null, isShowCameraButton(), mImageCallback);
        mRecyclerView.setAdapter(mAdapter);
        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.weimagepicker__toolbar_spinner,
                toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);
        mBucketSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        mBucketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                d("onItemSelected:" + position);
                mImageCallback.setCurrentBucket(mBuckets.get(position));
                getSupportLoaderManager().initLoader(position, null, ImagePickerActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });

        mManager = new ImageContentManager(this);
        mManager.prepare(new ImageContentManager.PrepareCallback() {
            @Override
            public void onPrepared() {
                mBuckets = mManager.getAllBucketList();
                BucketAdapter adapter = new BucketAdapter(ImagePickerActivity.this, mBuckets);
                mBucketSpinner.setAdapter(adapter);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSelectedBucket.putIntoBundle(outState);
        super.onSaveInstanceState(outState);
    }

    private boolean isShowCameraButton() {
        return showCameraButton;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        d("onCreateLoader:" + id);
        return mBuckets.get(id).createLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        d("onLoadFinished:" + loader.getId());
        if (loader.getId() == 0) {
            mAdapter.setShowCameraButton(isShowCameraButton());
        } else {
            mAdapter.setShowCameraButton(false);
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        d("onLoaderReset:" + loader.getId());
        mAdapter.swapCursor(null);
    }

    private void d(String text) {
        Log.d("ImagePickerActivity", text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weimagepicker__menu_image_picker, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);
        int count = mSelectedBucket.getCount();
        if (count > 0) {
            item.setVisible(true);
            item.setTitle(getString(R.string.weimagepicker__action_done, count));
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            done(mSelectedBucket.toUriArray());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageCallback.handleCameraResult(requestCode, resultCode, data);
    }

    protected void done(@NonNull Uri[] uris) {
        if (uris.length > 0) {
            Intent data = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ClipData clipData = ClipData.newUri(getContentResolver(), "ImagePicker", uris[0]);
                for (int i = 1; i < uris.length; i++) {
                    clipData.addItem(new ClipData.Item(uris[i]));
                }
                data.setClipData(clipData);
            }
            mSelectedBucket.putIntoIntent(data);
            setResult(RESULT_OK, data);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
