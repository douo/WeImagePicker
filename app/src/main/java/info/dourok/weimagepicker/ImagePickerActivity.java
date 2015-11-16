package info.dourok.weimagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ImagePickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView mRecyclerView;
    ImageContentManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mManager = new ImageContentManager(this);
        mManager.prepare(new ImageContentManager.PrepareCallback() {
            @Override
            public void onPrepared() {
                getSupportLoaderManager().initLoader(0, null, ImagePickerActivity.this);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mManager.createLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ImageAdapter adapter = new ImageAdapter(this, data, false);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
