package info.dourok.weimagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

public class ImagePickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView mRecyclerView;
    Spinner mBucketSpinner;
    ImageContentManager mManager;
    List<Bucket> mBuckets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner,
                toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);
        mBucketSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        mBucketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("onItemSelected:" + position);
                getSupportLoaderManager().initLoader(position, null, ImagePickerActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });

        mManager = new ImageContentManager(this);
        mManager.prepare(new ImageContentManager.PrepareCallback() {
            @Override
            public void onPrepared() {
                mBuckets = mManager.getAllBucketList();
                BucketAdapter adapter = new BucketAdapter(ImagePickerActivity.this, mBuckets);
                mBucketSpinner.setAdapter(adapter);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        System.out.println("onCreateLoader:" + id);
        return mBuckets.get(id).createLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println("onLoadFinished:" + loader.getId());
        ImageAdapter adapter = new ImageAdapter(this, data, false);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("onLoaderReset:" + loader.getId());
    }
}
