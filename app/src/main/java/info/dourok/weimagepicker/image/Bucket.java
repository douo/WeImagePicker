package info.dourok.weimagepicker.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

/**
 * Bucket 的子类必须提供一个无参数的构造函数
 * Created by John on 2015/11/17.发
 */
public abstract class Bucket {


    public Bucket() {
    }

    public abstract String getName();

    public abstract int getCount();

    public abstract long getFirstImageId();

    public abstract CursorLoader createLoader(Context context);


    public void putIntoIntent(Intent intent) {
        Bundle bundle = new Bundle();
        putIntoBundle(bundle);
        intent.putExtras(bundle);
    }

    public abstract void putIntoBundle(Bundle bundle);

    public void readFromIntent(Intent intent) {
        readFromBundle(intent.getExtras());
    }

    public abstract void readFromBundle(Bundle bundle);

    public static <T extends Bucket> T fromIntent(Intent intent, Class<T> tClass) {
        return Bucket.fromBundle(intent.getExtras(), tClass);
    }

    public static <T extends Bucket> T fromBundle(Bundle bundle, Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
            t.readFromBundle(bundle);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }


}
