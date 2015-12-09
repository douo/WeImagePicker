package info.dourok.weimagepicker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import info.dourok.weimagepicker.image.Bucket;

/**
 * Created by John on 2015/12/9.
 */
abstract class ImagePicker {
    protected ImagePickerActivity mActivity;

    public ImagePicker(ImagePickerActivity activity) {
        this.mActivity = activity;
    }

    protected abstract void initUi();

    protected View findViewById(int id) {
        return mActivity.findViewById(id);
    }


    public abstract void onLoadFinished(Loader<Cursor> loader, Cursor data);

    public abstract void onLoaderReset(Loader<Cursor> loader);

    public abstract int getLayoutId();

    public abstract boolean onPrepareOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public abstract boolean onCreateOptionsMenu(Menu menu);

    public abstract void prepared(List<Bucket> mBuckets);

    public abstract boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    public abstract String getName();

    protected void d(String msg) {
        Log.d(getName(), msg);
    }
}
