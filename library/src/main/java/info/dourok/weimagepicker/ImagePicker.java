package info.dourok.weimagepicker;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.StyleRes;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import info.dourok.weimagepicker.image.Bucket;

/**
 * ImagePicker 负责对数据的展示
 * Created by DouO on 2015/12/9.
 */
public abstract class ImagePicker {
    protected ImagePickerActivity mContext;

    public ImagePicker(ImagePickerActivity activity) {
        this.mContext = activity;
    }

    /**
     * Init ui here, call from Activity#onCreate
     */
    protected abstract void onViewCreated(View contentView);

    protected View findViewById(int id) {
        return mContext.findViewById(id);
    }


    public abstract void onLoadFinished(Loader<Cursor> loader, Cursor data);

    public abstract void onLoaderReset(Loader<Cursor> loader);

    public abstract int getLayoutId();


    @StyleRes
    public int getPreviewTheme() {
        return -1; //NO_THEME
    }

    public abstract boolean onPrepareOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public abstract boolean onCreateOptionsMenu(Menu menu);

    /**
     * Call when all bucket initialized
     *
     * @param buckets bucket list
     */
    public abstract void prepared(List<Bucket> buckets);

    public abstract boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    public abstract String getName();

    protected void d(String msg) {
        Log.d(getName(), msg);
    }
}
