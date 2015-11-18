package info.dourok.weimagepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import info.dourok.weimagepicker.image.ImageContentManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageContentManager imageContentManager = new ImageContentManager(this);
    }


}
