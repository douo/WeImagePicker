package info.dourok.weimagepicker.image;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2015/11/18.
 */
public class SelectedBucket extends Bucket {
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
        StringBuilder sb = new StringBuilder();
        sb.append(MediaStore.Images.Media._ID).append(" IN (");
        boolean firstTime = true;
        for (long id : selectedIds) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(",");
            }
            sb.append(id);
        }
        sb.append(")");
        return new CursorLoader(context, ImageContentManager.URI, ImageContentManager.PROJECTION, sb.toString(), null, null);
    }

    public long[] toArray() {
        long[] ids = new long[getCount()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = selectedIds.get(i);
        }
        return ids;
    }

    public Uri[] toUriArray() {
        Uri[] uris = new Uri[getCount()];
        for (int i = 0; i < uris.length; i++) {
            uris[i] = ContentUris.withAppendedId(ImageContentManager.URI, selectedIds.get(i));
        }
        return uris;
    }


    @Override
    public void putIntoBundle(Bundle bundle) {
        bundle.putLongArray(KEY_BUCKET, toArray());
    }

    @Override
    public void readFromBundle(Bundle bundle) {
        long ids[] = bundle.getLongArray(KEY_BUCKET);
        List<Long> list = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            list.add(ids[i]);
        }
        selectedIds = list;
    }

}
