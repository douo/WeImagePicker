package info.dourok.weimagepicker.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import info.dourok.lruimage.LruImage;
import info.dourok.lruimage.LruImageException;

/**
 * Created by John on 2015/11/18.
 */
public class LruThumbnail extends LruImage {
    ContentResolver mResolver;
    long origId;
    int kind;

    public LruThumbnail(ContentResolver resolver, long origId, int kind) {
        this.mResolver = resolver;
        this.origId = origId;
        this.kind = kind;
        setCacheLevel(LruImage.CACHE_LEVEL_MEMORY_CACHE);
    }

    public LruThumbnail(ContentResolver resolver, long origId) {
        this(resolver, origId, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    @Override
    protected Bitmap loadBitmap(Context context) throws LruImageException {
        return MediaStore.Images.Thumbnails.getThumbnail(mResolver, origId, kind, null);
    }

    @Override
    public String getKey() {
        return "LruThumbnail_" + origId + "_" + kind;
    }
}
