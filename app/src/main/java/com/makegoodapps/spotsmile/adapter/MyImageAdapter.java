package com.makegoodapps.spotsmile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.makegoodapps.spotsmile.R;
import com.makegoodapps.spotsmile.SpotBoardActivity;
import com.makegoodapps.spotsmile.widget.SquareImageView;

public class MyImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mSdHappyImages;

    public MyImageAdapter(Context c, String[] images) {
        mContext = c;
        mSdHappyImages = images;
    }

    @Override
    public int getCount() {

        return mSdHappyImages.length;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            gridItem = inflater.inflate(R.layout.cell_spotboard_grid, parent, false);
            SquareImageView imageView = (SquareImageView) gridItem.findViewById(R.id.grid_image);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(SpotBoardActivity.SDCARD_SPOT_SMILE_HAPPY
                    + mSdHappyImages[position], options);

            imageView.setTag(SpotBoardActivity.SDCARD_SPOT_SMILE_HAPPY + mSdHappyImages[position]);
            imageView.setImageBitmap(bitmap);
            imageView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_remove));
        } else {
            gridItem = convertView;
        }
        return gridItem;
    }

}
