package com.makegoodapps.spotsmile;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.makegoodapps.spotsmile.adapter.MyImageAdapter;
import com.makegoodapps.spotsmile.resultdatabase.MySQLiteHelper;
import com.makegoodapps.spotsmile.resultdatabase.ResultSource;
import com.makegoodapps.spotsmile.widget.RemovableAd;
import com.makegoodapps.spotsmile.widget.SquareImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubColumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A column and line chart
 * Created by WillsPC on 2014/12/30.
 */
public class MyImageActivity extends ActionBarActivity {

    private ArrayList<String> mRemoveImageList;
    private GridView mMyImageGrid;
    private Button mMyImagePositive;
    private Button mMyImageNegative;
    private MyImageAdapter mMyImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_image);

        if (mRemoveImageList == null) {
            mRemoveImageList = new ArrayList<>();
        } else {
            mRemoveImageList.clear();
        }

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMyImagePositive = (Button) findViewById(R.id.myImagePositive);
        mMyImageNegative = (Button) findViewById(R.id.myImageNegative);
        mMyImageGrid = (GridView) findViewById(R.id.myImageGrid);
        File sdSmileDir = new File(SpotBoardActivity.SDCARD_SPOT_SMILE_HAPPY);
        if (sdSmileDir.exists() && sdSmileDir.list().length > 0) {
            mMyImageAdapter = new MyImageAdapter(this, sdSmileDir.list());
            mMyImageGrid.setAdapter(mMyImageAdapter);
        } else {
            mMyImagePositive.setEnabled(false);
        }
        resetDialogButton();

        mMyImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean toRemove = mMyImagePositive.getText()
                        .equals(getResources().getString(R.string.dialog_my_image_remove))
                        && !view.isActivated();
                view.setActivated(toRemove);
                SquareImageView squareImageView = (SquareImageView) ((LinearLayout) view).getChildAt(0);
                if (toRemove) {
                    mRemoveImageList.add((String) squareImageView.getTag());
                } else {
                    mRemoveImageList.remove(squareImageView.getTag());
                }
            }
        });


    }

    private View.OnClickListener mEditMyImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMyImagePositive.setText(R.string.dialog_my_image_remove);
            mMyImageNegative.setText(android.R.string.cancel);
            mMyImagePositive.setOnClickListener(mRemoveMyImage);
            mMyImageNegative.setOnClickListener(mCancelMyImage);
        }
    };

    private View.OnClickListener mRemoveMyImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (String path : mRemoveImageList) {
                File file = new File(path);
                file.delete();
            }

            mRemoveImageList.clear();
            File sdSmileDir = new File(SpotBoardActivity.SDCARD_SPOT_SMILE_HAPPY);
            mMyImageAdapter = new MyImageAdapter(MyImageActivity.this, sdSmileDir.list());
            mMyImageGrid.setAdapter(mMyImageAdapter);
            mMyImagePositive.setEnabled(sdSmileDir.list().length > 0);
            resetDialogButton();
        }
    };

    private View.OnClickListener mQuitMyImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener mCancelMyImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRemoveImageList.clear();
            resetDialogButton();
        }
    };

    private void resetDialogButton() {
        mMyImageGrid.setAdapter(mMyImageAdapter);
        mMyImagePositive.setText(R.string.dialog_my_image_edit);
        mMyImageNegative.setText(R.string.dialog_my_image_quit);
        mMyImagePositive.setOnClickListener(mEditMyImage);
        mMyImageNegative.setOnClickListener(mQuitMyImage);
    }


}
