package info.dourok.weimagepicker;

import android.view.MenuItem;
import android.view.View;

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
        findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageCallback.previewSelected();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.weimagepicker__activity_wechat_mage_picker;
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
