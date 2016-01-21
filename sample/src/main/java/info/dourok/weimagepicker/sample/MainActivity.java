package info.dourok.weimagepicker.sample;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import info.dourok.weimagepicker.PickerBuilder;
import info.dourok.weimagepicker.image.Bucket;
import info.dourok.weimagepicker.image.SelectedBucket;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            debugTheme(this, getTheme());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private static void debugTheme(Activity activity, Resources.Theme theme) throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        String TAG = "Theme";
        Resources resources = activity.getResources();
        Method mGetAllAttributes = Resources.Theme.class.getMethod("getAllAttributes");
        Field fThemeResId = Resources.Theme.class.getDeclaredField("mThemeResId");
        fThemeResId.setAccessible(true);
        Field fKey = Resources.Theme.class.getDeclaredField("mKey");
        int[] resIDs = (int[]) mGetAllAttributes.invoke(theme);
        int themeResId = fThemeResId.getInt(theme);
        TypedArray array = theme.obtainStyledAttributes(resIDs);
        array.getString(0);
        Log.d(TAG, String.format("0x%08X:", themeResId) + resources.getResourceName(themeResId));
        Log.d(TAG, "==========================");
        for (int i = 0; i < resIDs.length; i++) {
            Log.d(TAG, String.format("0x%08X:", resIDs[i]) + resources.getResourceName(resIDs[i]) + ":" + array.getString(i));
        }

    }


    public void pickerBuilder(View view) {
        Intent i = new PickerBuilder(this).setSelectedImageLimit(1).setShowCameraButton(true).useWeChatTheme().createIntent();
        startActivityForResult(i, REQUEST_PICK);
    }

    public void generalIntent(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, REQUEST_PICK);
    }

    public void customPicker(View view) {
        startActivity(new Intent(this, CustomPickerActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
            SelectedBucket bucket = Bucket.fromIntent(data, SelectedBucket.class);
            for (long id : bucket.toArray()) {
                Log.d("MainActivity", "bucket:" + id);
            }
            StringBuilder builder = new StringBuilder();
            for (Uri uri : bucket.toUriArray()) {
                builder.append(uri.toString()).append('\n');
            }
            Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Log.d("MainActivity", clipData.getItemAt(i).getUri().toString());
                }
            }
        }

    }

}
