package info.dourok.weimagepicker;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.ImageContentManager;
import info.dourok.weimagepicker.image.SelectedBucket;

public class ImagePickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x1;
    ImageContentManager mManager;
    List<Bucket> mBuckets;
    SelectedBucket mSelectedBucket;
    ImagePicker mPicker;
    public final static String EXTRA_SHOW_CAMERA_BUTTON = "info.dourok.weimagepicker.extra.SHOW_CAMERA_BUTTON";
    public final static String EXTRA_SELECTED_IMAGE_LIMIT = "info.dourok.weimagepicker.extra.SELECTED_IMAGE_LIMIT";
    public final static String EXTRA_ALLOW_MULTIPLE = "info.dourok.weimagepicker.extra.ALLOW_MULTIPLE";
    public final static String EXTRA_PICKER = "info.dourok.weimagepicker.extra.PICKER";
    private boolean showCameraButton;
    private int maxImageNumber;
    private boolean allowMultiple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedBucket = Bucket.fromBundle(savedInstanceState, SelectedBucket.class);
        } else {
            mSelectedBucket = new SelectedBucket();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {

                    // No explanation needed, we can request the permission.


                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                init();
            }
        } else {
            init();
        }

    }

    private void init() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        allowMultiple = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, intent.getBooleanExtra(EXTRA_ALLOW_MULTIPLE, false));
        showCameraButton = intent.getBooleanExtra(EXTRA_SHOW_CAMERA_BUTTON, Intent.ACTION_GET_CONTENT.equals(action));
        maxImageNumber = intent.getIntExtra(EXTRA_SELECTED_IMAGE_LIMIT, allowMultiple ? 0 : 1);

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
            mPicker = new MaterialImagePicker(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    init();
                } else {
                    Toast.makeText(this, "Failed to request permission", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

        }
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
    protected int getMaxImageNumber() {
        return maxImageNumber;
    }

    protected final boolean hasMaxLimit() {
        return getMaxImageNumber() > 0;
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
                ClipData clipData = ClipData.newUri(getContentResolver(), "WeImagePicker", uris[0]);
                for (int i = 1; i < uris.length; i++) {
                    clipData.addItem(new ClipData.Item(uris[i]));
                }
                data.setClipData(clipData);
            }
            data.setData(uris[0]);// single content supported
            mSelectedBucket.putIntoIntent(data);
            setResult(RESULT_OK, data);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
