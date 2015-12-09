package info.dourok.weimagepicker;

import android.view.MenuItem;

/**
 * Created by John on 2015/12/9.
 */
public class WeChatImagePicker extends MaterialImagePicker {

    public WeChatImagePicker(ImagePickerActivity activity) {
        super(activity);
    }

    @Override
    protected void initUi() {
        super.initUi();
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mActivity.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
