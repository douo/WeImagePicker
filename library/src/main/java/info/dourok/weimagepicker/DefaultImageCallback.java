package info.dourok.weimagepicker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import info.dourok.weimagepicker.adapter.ImageViewHolder;
import info.dourok.weimagepicker.adapter.OnImageCallback;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.ImageContentManager;
import info.dourok.weimagepicker.image.SelectedBucket;

/**
 * Created by John on 2015/11/21.
 */
public abstract class DefaultImageCallback implements OnImageCallback {
    private static final int REQUEST_TAKE_PHOTO = 0x12;
    private static final String TAG = "DefaultImageCallback";
    Activity mContext;
    SelectedBucket mSelectedBucket;
    Bucket currentBucket;
    private int maxImageCount;

    public DefaultImageCallback(Activity context, SelectedBucket selectedBucket, int maxImageCount) {
        mContext = context;
        mSelectedBucket = selectedBucket;
        currentBucket = selectedBucket;
        this.maxImageCount = maxImageCount;
    }

    public void setCurrentBucket(Bucket currentBucket) {
        this.currentBucket = currentBucket;
    }

    @Override
    public void onTakePicture() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onImageSelect(ImageViewHolder holder, long imageId, int position) {
        if (maxImageCount <= 0 || mSelectedBucket.getCount() < maxImageCount) {
            boolean selected = mSelectedBucket.toggle(imageId);
            holder.setSelected(selected);
        } else {
            if (mSelectedBucket.isSelected(imageId)) {
                mSelectedBucket.unselect(imageId);
                holder.setSelected(false);
            }
        }
    }

    @Override
    public void onImageClick(ImageViewHolder holder, long imageId, int position) {
        mContext.startActivity(ImagePreviewActivity.createIntentForBucket(mContext, currentBucket, mSelectedBucket, position));
    }

    @Override
    public boolean isSelected(ImageViewHolder holder, long imageId) {
        return mSelectedBucket.isSelected(imageId);
    }

    private File createNewPhotoFile() {
        return new File(String.valueOf(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)) + File.separator + "Image" + "_" + System.currentTimeMillis() + ".jpg");
    }

    File photoFile;

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            // Continue only if the File was successfully created
            photoFile = createNewPhotoFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            mContext.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    public abstract void onCameraPhotoSaved(Uri uri);

    public boolean handleCameraResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, photoFile.getAbsolutePath());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, photoFile.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, photoFile.getAbsolutePath());
            Uri uri = mContext.getContentResolver().insert(ImageContentManager.URI, values);
            onCameraPhotoSaved(uri);
            return true;

//            MediaScannerConnection.scanFile(mContext,
//                    new String[]{
//                            photoFile.toString()
//                    }, new String[]{
//                            MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")
//                    },
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        @Override
//                        public void onScanCompleted(final String path, final Uri uri) {
//                            mContext.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.d(TAG, uri.toString());
//                                    onCameraPhotoSaved(uri);
//                                }
//                            });
//
//                        }
//                    });
//            return true;
        } else {
            photoFile = null;
            return false;
        }
    }
}
