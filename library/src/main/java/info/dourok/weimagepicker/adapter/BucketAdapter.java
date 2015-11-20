package info.dourok.weimagepicker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.dourok.weimagepicker.R;
import info.dourok.weimagepicker.image.Bucket;

/**
 * Created by John on 2015/11/17.
 */
public class BucketAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bucket> mBucketList;

    public BucketAdapter(Context context, List<Bucket> buckets) {
        this.mContext = context;
        this.mBucketList = buckets;
    }

    @Override
    public int getCount() {
        return mBucketList.size();
    }

    @Override
    public Bucket getItem(int position) {
        return mBucketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    final class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView count;
        public CheckBox checkbox;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.weimagepicker__item_bucket, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Bucket bucket = getItem(position);
        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), bucket.getFirstImageId(), MediaStore.Images.Thumbnails.MINI_KIND, null);
        holder.image.setImageBitmap(bm);
        holder.count.setText(bucket.getCount() + "张");
        holder.name.setText(bucket.getName());
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || !convertView.getTag().toString().equals("NON_DROPDOWN")) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.
                    weimagepicker__item_bucket_spinner, parent, false);
            convertView.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());
        return convertView;
    }
}
