package info.dourok.weimagepicker;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
public class WeChatImagePicker extends ImagePicker {
    RecyclerView mRecyclerView;
    Spinner mBucketSpinner;
    ImageAdapter mAdapter;
    DefaultImageCallback mImageCallback;
    TextView mPreviewBtn;

    public WeChatImagePicker(ImagePickerActivity activity) {
        super(activity);
    }

    @Override
    protected void onViewCreated(View contentView) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext.setSupportActionBar(toolbar);
        mContext.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                mContext.setTitle(mContext.getBuckets().get(position).getName());
                mContext.switchBucket(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });
        mPreviewBtn = (TextView) findViewById(R.id.preview);
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageCallback.previewSelected();
            }
        });

        refreshPreviewButton();
    }

    private void refreshPreviewButton() {
        if (mContext.getSelectedBucket().getCount() == 0) {
            mPreviewBtn.setEnabled(false);
            mPreviewBtn.setText(R.string.weimagepicker__name_preview_no_selection);
        } else {
            mPreviewBtn.setEnabled(true);
            mPreviewBtn.setText(mContext.getString(R.string.weimagepicker__name_preview, mContext.getSelectedBucket().getCount()));
        }
    }

    protected DefaultImageCallback createImageCallback() {
        return new DefaultImageCallback(mContext, mContext.getSelectedBucket(), mContext.getMaxImageNumber()) {
            @Override
            public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
                super.onImageSelect(holder, imageId, position);
                WeChatImagePicker.this.mContext.supportInvalidateOptionsMenu();
                refreshPreviewButton();
            }

            @Override
            public void onCameraPhotoSaved(Uri uri) {
                mSelectedBucket.add(ContentUris.parseId(uri));
                WeChatImagePicker.this.mContext.onFinish(mSelectedBucket.toUriArray());
            }

            @Override
            public void onSelectedBucketUpdated(SelectedBucket selectedBucket) {
                mAdapter.notifyDataSetChanged();
                WeChatImagePicker.this.mContext.supportInvalidateOptionsMenu();
                refreshPreviewButton();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
        return R.layout.weimagepicker__activity_wechat_mage_picker;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);
        Button btn = (Button) item.getActionView();
        int count = mContext.getSelectedBucket().getCount();
        if (count > 0) {
            btn.setEnabled(true);
            if (mContext.hasMaxLimit()) {
                btn.setText(mContext.getString(R.string.weimagepicker__action_done_limit, count, mContext.getMaxImageNumber()));
            } else {
                btn.setText(mContext.getString(R.string.weimagepicker__action_done, count));
            }
        } else {
            btn.setEnabled(false);
            btn.setText(R.string.weimagepicker__action_done_no_selection);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mContext.finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mContext.getMenuInflater().inflate(R.menu.weimagepicker__menu_wechat_image_picker, menu);
        MenuItem item = menu.findItem(R.id.action_done);
        Button btn = (Button) item.getActionView();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onFinish(mContext.getSelectedBucket().toUriArray());
            }
        });
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
        return "WeChatImagePicker";
    }


}
