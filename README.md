一个模仿微信图片选择功能的图片选择器，支持多选和拍照。

### 快速使用

在 `AndroidManifest.xml` 中添加， activity 的声明代码：

```
   <activity
        android:name="info.dourok.weimagepicker.ImagePickerActivity"
        android:label="@string/weimagepicker__title_activity_image_picker"
        android:theme="@style/AppTheme.NoActionBar"/>
   <activity
        android:name="info.dourok.weimagepicker.ImagePreviewActivity"
        android:theme="@style/AppTheme.NoActionBar" />
```

如此便可在 Activity 中使用，调用只需一句：

```
startActivityForResult(new PickerBuilder(this).createIntent(), REQUEST_PICK);
```

在 onActivityResult 中处理完成的结果：

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
             SelectedBucket bucket = Bucket.fromIntent(data, SelectedBucket.class);
        }
}
```

SelectedBucket 封装了用户选择的图片 Uri 列表，提供多个便利方法，方便加载图片。

- SelectedBucket#toArray 返回当前所选图片的 id 列表
- SelectedBucket#toUriArray 返回当前所选图片的 uri 列表
- SelectedBucket#createLoader(Context) 返回当前所选图片的 CursorLoader

PickerBuilder 可以用来配置图片选择器，比如限制选择图片张数，是否显示拍照按钮，甚至可以切换成微信主题


### 通用的调用方法

WeImagePicker 也支持一般获取图片 Intent 的调用方法。首先，需要更改一下 ImagePickerActivity 的声明方法：

```
<activity
    android:name="info.dourok.weimagepicker.ImagePickerActivity"
    android:label="@string/weimagepicker__title_activity_image_picker"
    android:theme="@style/AppTheme.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.GET_CONTENT" />

        <category android:name="android.intent.category.DEFAULT" />

        <data android:mimeType="image/*" />
    </intent-filter>
</activity>
```

然后可以向我们一般请求系统图片那样声明 Intent

```
 Intent i = new Intent(Intent.ACTION_GET_CONTENT);
 i.setType("image/*");
 i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 支持允许多选的选项
 startActivityForResult(i, REQUEST_PICK);
```

在 onActivityResult 处理结果：

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData(); //单选

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) { //多选
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Log.d("MainActivity", clipData.getItemAt(i).getUri().toString());
                }
            }
        }
    }

### 不通过 Intent

通过继承 ImagePickerActivity 可以不用 startActivityForResult 的机制在代码中直接使用选择结果

```
public class CustomPickerActivity extends ImagePickerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getSelectedImageLimit() {
        return 0; // 不限制张数的多选
    }

    @Override
    protected boolean isShowCameraButton() {
        return true; // 显示拍照按钮
    }

    /**
    * 用户完成选择，处理选择结果
    **/
    @Override
    public void onFinish(@NonNull Uri[] uris) {
        StringBuilder builder = new StringBuilder();
        for (Uri uri : uris) {
            builder.append(uri.toString()).append('\n');
        }
        Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
    }
}
```

### 自定义界面

要自定义界面不建议继承 ImagePickerActivity ，最好是实现新的 ImagePicker 并传递给 ImagePickerActivity 更为方便，这一块还在改进中...