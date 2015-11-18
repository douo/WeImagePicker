package info.dourok.weimagepicker.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2015/11/18.
 */
public class SelectedBucket implements Bucket {
    private List<Long> selectedIds;
    private final static String KEY_BUCKET = "info.dourok.weimagepicker.image.SelectedBucket";

    private SelectedBucket(List<Long> list) {
        selectedIds = list;
    }

    public SelectedBucket() {
        selectedIds = new ArrayList<>();
    }

    public void add(long id) {
        selectedIds.add(id);
    }


    public boolean select(long id) {
        if (selectedIds.contains(id)) {
            return false;
        } else {
            add(id);
            return true;
        }
    }

    public boolean unselect(long id) {
        return selectedIds.remove(id);
    }

    /**
     * toggle select status
     *
     * @param id
     * @return true if select otherwise is unselect
     */
    public boolean toggle(long id) {
        if (!select(id)) {
            unselect(id);
            return false;
        } else {
            return true;
        }
    }

    public boolean isSelected(long id) {
        return selectedIds.contains(id);
    }


    @Override
    public String getName() {
        return "Selected";
    }

    @Override
    public int getCount() {
        return selectedIds.size();
    }

    @Override
    public long getFirstImageId() {
        if (getCount() > 0) {
            return selectedIds.get(0);
        } else {
            return 0;
        }
    }

    @Override
    public CursorLoader createLoader(Context context) {
        //TODO
        return null;
    }

    public long[] toArray() {
        long[] ids = new long[getCount()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = selectedIds.get(i);
        }
        return ids;
    }

    public void putIntoIntent(Intent intent) {
        intent.putExtra(KEY_BUCKET, toArray());
    }

    public void putIntoBundle(Bundle bundle) {
        bundle.putLongArray(KEY_BUCKET, toArray());
    }

    public void readFromIntent(Intent intent) {
        readFromBundle(intent.getExtras());
    }

    public void readFromBundle(Bundle bundle) {
        long ids[] = bundle.getLongArray(KEY_BUCKET);
        List<Long> list = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            list.add(ids[i]);
        }
        selectedIds = list;
    }


    public static SelectedBucket getFromIntent(Intent intent) {
        return getFromBundle(intent.getExtras());
    }

    public static SelectedBucket getFromBundle(Bundle bundle) {
        long ids[] = bundle.getLongArray(KEY_BUCKET);
        List<Long> list = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            list.add(ids[i]);
        }
        return new SelectedBucket(list);
    }
}
