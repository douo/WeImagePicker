package info.dourok.weimagepicker;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.ImageContentManager;
import info.dourok.weimagepicker.image.SelectedBucket;

public class ImagePickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageContentManager mManager;
    List<Bucket> mBuckets;
    SelectedBucket mSelectedBucket;
    ImagePicker mPicker;
    public final static String EXTRA_SHOW_CAMERA_BUTTON = "info.dourok.weimagepicker.extra.SHOW_CAMERA_BUTTON";
    public final static String EXTRA_SELECTED_IMAGE_LIMIT = "info.dourok.weimagepicker.extra.SELECTED_IMAGE_LIMIT";
    public final static String EXTRA_ALLOW_MULTIPLE = "info.dourok.weimagepicker.extra.ALLOW_MULTIPLE";
    public final static String EXTRA_PICKER = "info.dourok.weimagepicker.extra.PICKER";
    private boolean showCameraButton;
    private int selectedImageLimit;
    private boolean allowMultiple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedBucket = Bucket.fromBundle(savedInstanceState, SelectedBucket.class);
        } else {
            mSelectedBucket = new SelectedBucket();
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        allowMultiple = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, intent.getBooleanExtra(EXTRA_ALLOW_MULTIPLE, false));
        showCameraButton = intent.getBooleanExtra(EXTRA_SHOW_CAMERA_BUTTON, Intent.ACTION_GET_CONTENT.equals(action));
        selectedImageLimit = intent.getIntExtra(EXTRA_SELECTED_IMAGE_LIMIT, allowMultiple ? 0 : 1);

        String pickerClass = intent.getStringExtra(EXTRA_PICKER);
        if (pickerClass != null) {
            try {
                mPicker = (ImagePicker) Class.forName(pickerClass).getConstructor(ImagePickerActivity.class).newInstance(this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (mPicker == null) {
            //mPicker = new MaterialImagePicker(this);
            mPicker = new WeChatImagePicker(this);
        }
        setContentView(mPicker.getLayoutId());
        mPicker.initUi();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        mime.getExtensionFromMimeType(type);
        mManager = new ImageContentManager(this, type);
        mManager.prepare(new ImageContentManager.PrepareCallback() {
            @Override
            public void onPrepared() {
                mBuckets = mManager.getAllBucketList();
                mPicker.prepared(mBuckets);
            }
        });
    }

    public SelectedBucket getSelectedBucket() {
        return mSelectedBucket;
    }

    public List<Bucket> getBuckets() {
        return mBuckets;
    }

    public void switchBucket(int position) {
        getSupportLoaderManager().initLoader(position, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSelectedBucket.putIntoBundle(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * @return 是否显示调用相机按钮
     */
    protected boolean isShowCameraButton() {
        return showCameraButton;
    }

    /**
     * @return 大于 0 表示最多可选择的最大图片张数，小于等于零表示无限制图片张数
     */
    protected int getSelectedImageLimit() {
        return selectedImageLimit;
    }

    protected final boolean hasMaxLimit() {
        return getSelectedImageLimit() > 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        d("onCreateLoader:" + id);
        return mBuckets.get(id).createLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        d("onLoadFinished:" + loader.getId());
        mPicker.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        d("onLoaderReset:" + loader.getId());
        mPicker.onLoaderReset(loader);
    }

    private void d(String text) {
        Log.d("ImagePickerActivity", text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mPicker.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mPicker.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mPicker.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPicker.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void done(@NonNull Uri[] uris) {
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
