package info.dourok.weimagepicker.image;

import android.content.Context;
import android.support.v4.content.CursorLoader;

/**
 * Created by John on 2015/11/17.
 */
public interface Bucket {
    String getName();

    int getCount();

    long getFirstImageId();

    CursorLoader createLoader(Context context);

}
