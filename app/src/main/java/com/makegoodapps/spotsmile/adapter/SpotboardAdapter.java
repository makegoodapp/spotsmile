package com.makegoodapps.spotsmile.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.makegoodapps.spotsmile.R;
import com.makegoodapps.spotsmile.SpotBoardActivity;
import com.makegoodapps.spotsmile.widget.SquareImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Display happy and angry faces to users
 * Created by WillsPC on 2014/12/20.
 */
public class SpotBoardAdapter extends BaseAdapter {
    private final static int GRID_IMAGE_NUMBER = 9;

    private Context mContext;
    private final TypedArray mImageAngryIds;
    private final TypedArray mImageHappyIds;
    private String[] mSdHappyImages;
    private int mHappyImagePos;
    private ArrayList<Integer> mHappyList = new ArrayList<>();
    private ArrayList<Integer> mAngryList = new ArrayList<>();
    private ArrayList<Integer> mSdHappyList = new ArrayList<>();

    public SpotBoardAdapter(Context c, TypedArray imageAngryIds, TypedArray imageHappyIds, String[] images) {
        mContext = c;
        mImageAngryIds = imageAngryIds;
        mImageHappyIds = imageHappyIds;
        mSdHappyImages = images;

        setImgHappyPos();
        setAngryList();
        setHappyList();
    }

    public SpotBoardAdapter(Context c, TypedArray imageAngryIds, TypedArray imageHappyIds) {
        mContext = c;
        mImageAngryIds = imageAngryIds;
        mImageHappyIds = imageHappyIds;
        mSdHappyImages = null;

        setImgHappyPos();
        setAngryList();
        setHappyList();
    }

    @Override
    public int getCount() {

        return GRID_IMAGE_NUMBER;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public int getImageAngryPos() {
        return mHappyImagePos;
    }

    public void setImgHappyPos() {
        Random r = new Random();

        int temHappyPos = mHappyImagePos;
        do {
            mHappyImagePos = r.nextInt(GRID_IMAGE_NUMBER);
        } while (mHappyImagePos == temHappyPos);
    }

    public void setAngryList() {
        if (mAngryList.size() < GRID_IMAGE_NUMBER - 1) {
            mAngryList.clear();
            for (int i = 0; i < mImageAngryIds.length(); ++i) mAngryList.add(i);
            Collections.shuffle(mAngryList);
        }
    }

    public void setHappyList() {
        if (mHappyList.size() == 0) {
            for (int i = 0; i < mImageHappyIds.length(); ++i) mHappyList.add(i);
            Collections.shuffle(mHappyList);
        }

        if (mSdHappyImages != null && mSdHappyList.size() == 0) {
            for (int i = 0; i < mSdHappyImages.length; ++i) mSdHappyList.add(i);
            Collections.shuffle(mSdHappyList);
        }
    }

    public void clearHappyList() {
        mHappyList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            gridItem = inflater.inflate(R.layout.cell_spotboard_grid, parent, false);
            SquareImageView imageView = (SquareImageView) gridItem.findViewById(R.id.grid_image);

            if (position == mHappyImagePos) {
                if (mSdHappyImages != null) {
                    double displayChance = Math.random();
                    double defaultImageChance = Double.valueOf(mImageHappyIds.length())
                            / Double.valueOf((mImageHappyIds.length() + mSdHappyImages.length));
                    if (displayChance < defaultImageChance) {
                        setHappyView(imageView);
                    } else {
                        setSdHappyView(imageView);
                    }
                } else {
                    setHappyView(imageView);
                }
                imageView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_happy));

            } else {
                imageView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_non_happy));
                imageView.setImageResource(mImageAngryIds.getResourceId(mAngryList.get(0), -1));
                mAngryList.remove(0);
            }

        } else {
            gridItem = convertView;
        }
        return gridItem;
    }

    public void setSdHappyImages(String[] images) {
        mSdHappyImages = images;
        setHappyList();
    }

    private void setHappyView(SquareImageView imageView) {
        imageView.setImageResource(mImageHappyIds.getResourceId(mHappyList.get(0), -1));
        mHappyList.remove(0);
    }

    private void setSdHappyView(SquareImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(SpotBoardActivity.SDCARD_SPOT_SMILE_HAPPY
                + mSdHappyImages[mSdHappyList.get(0)], options);
        mSdHappyList.remove(0);

        imageView.setImageBitmap(bitmap);
    }

}
