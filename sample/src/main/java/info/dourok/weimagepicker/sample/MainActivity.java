package info.dourok.weimagepicker.sample;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import info.dourok.weimagepicker.PickerBuilder;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.SelectedBucket;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {
        startActivityForResult(new PickerBuilder(this).setSelectedImageLimit(1).setShowCameraButton(true).createIntent(), REQUEST_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
            SelectedBucket bucket = Bucket.fromIntent(data, SelectedBucket.class);
            for (long id : bucket.toArray()) {
                Log.d("MainActivity", "bucket:" + id);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Log.d("MainActivity", clipData.getItemAt(i).getUri().toString());
                }
            }
        }
    }
}
