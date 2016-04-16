package info.dourok.weimagepicker.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import info.dourok.weimagepicker.ImagePickerActivity;

public class CustomPickerActivity extends ImagePickerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getMaxImageNumber() {
        return 0; // allow no limited multiple selction
    }

    @Override
    protected boolean isShowCameraButton() {
        return true;
    }

    @Override
    public void done(@NonNull Uri[] uris) {
        StringBuilder builder = new StringBuilder();
        for (Uri uri : uris) {
            builder.append(uri.toString()).append('\n');
        }
        Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
    }
}
