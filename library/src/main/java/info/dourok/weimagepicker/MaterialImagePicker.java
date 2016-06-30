package info.dourok.weimagepicker;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.support.v4.util.DebugUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

import info.dourok.weimagepicker.adapter.BucketAdapter;
import info.dourok.weimagepicker.adapter.ImageAdapter;
import info.dourok.weimagepicker.adapter.ImageViewHolder;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.DeviceImageBucket;
import info.dourok.weimagepicker.image.SelectedBucket;

/**
 * Created by John on 2015/12/9.
 */
public class MaterialImagePicker extends ImagePicker {
    RecyclerView mRecyclerView;
    Spinner mBucketSpinner;
    ImageAdapter mAdapter;
    DefaultImageCallback mImageCallback;

    public MaterialImagePicker(ImagePickerActivity activity) {
        super(activity);
    }

    @Override
    protected void onViewCreated(View contentView) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext.setSupportActionBar(toolbar);
        mContext.getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                            int space = mContext.getResources().getDimensionPixelSize(R.dimen.weimagepicker__grid_item_margin);

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
        mImageCallback = createImageCallback();
        mAdapter = new ImageAdapter(mContext.getSupportActionBar().getThemedContext(), null, mContext.isShowCameraButton(), mImageCallback);
        mRecyclerView.setAdapter(mAdapter);
        mBucketSpinner = (Spinner) findViewById(R.id.toolbar_spinner);
        mBucketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                d("onItemSelected:" + position);
                mImageCallback.setCurrentBucket(mContext.getBuckets().get(position));
                mContext.switchBucket(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });
    }

    protected DefaultImageCallback createImageCallback() {
        return new DefaultImageCallback(mContext, mContext.getSelectedBucket(), mContext.getMaxImageNumber()) {
            @Override
            public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
                super.onImageSelect(holder, imageId, position);
                MaterialImagePicker.this.mContext.supportInvalidateOptionsMenu();
            }

            @Override
            public void onCameraPhotoSaved(Uri uri) {
                mSelectedBucket.add(ContentUris.parseId(uri));
                MaterialImagePicker.this.mContext.onFinish(mSelectedBucket.toUriArray());
            }

            @Override
            public void onSelectedBucketUpdated(SelectedBucket selectedBucket) {
                mAdapter.notifyDataSetChanged();
                MaterialImagePicker.this.mContext.supportInvalidateOptionsMenu();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        StringBuilder sb = new StringBuilder();
        DebugUtils.buildShortClassTag(loader, sb);
        System.out.println(sb);
        if (mContext.getCurrentBucket() instanceof DeviceImageBucket) {
            mAdapter.setShowCameraButton(mContext.isShowCameraButton());
        } else {
            mAdapter.setShowCameraButton(false);
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.weimagepicker__activity_image_picker;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);
        int count = mContext.getSelectedBucket().getCount();
        if (count > 0) {
            item.setVisible(true);
            if (mContext.hasMaxLimit()) {
                item.setTitle(mContext.getString(R.string.weimagepicker__action_done_limit, count, mContext.getMaxImageNumber()));
            } else {
                item.setTitle(mContext.getString(R.string.weimagepicker__action_done, count));
            }
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            mContext.onFinish(mContext.getSelectedBucket().toUriArray());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mContext.getMenuInflater().inflate(R.menu.weimagepicker__menu_image_picker, menu);
        return true;
    }

    @Override
    public void prepared(List<Bucket> buckets) {
        BucketAdapter adapter = new BucketAdapter(mContext, buckets);
        mBucketSpinner.setAdapter(adapter);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mImageCallback.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String getName() {
        return "MaterialImagePicker";
    }

}
