package com.makegoodapps.spotsmile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.makegoodapps.easyratingdialog_library.EasyRatingDialog;
import com.makegoodapps.spotsmile.adapter.SpotBoardAdapter;
import com.makegoodapps.spotsmile.resultdatabase.ResultData;
import com.makegoodapps.spotsmile.resultdatabase.ResultSource;
import com.makegoodapps.spotsmile.widget.RemovableAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SpotBoardActivity extends Activity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int TIME_PROMPT_MILLIS = 1000;
    public static final String SDCARD_SPOT_SMILE_HAPPY = "/sdcard/SpotSmile/happy/";

    private GridView mSpotGrid;
    private TextView mSpotTitle;
    private Button mStart;
    private Button mReview;
    private Button mManageMyImage;
    private View mAddImageContainer;
    private CountDownTimer mCountDownTimer;
    private int mHappyCount;
    private ResultSource mResultSource = null;
    private SpotBoardAdapter mSpotBoardAdapter;
    private RemovableAd mRemovableAd;
    private Uri mImageCaptureUri;
    private EasyRatingDialog mEasyRatingDialog;
    private boolean mIsStorageAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soptboard);
        mRemovableAd = (RemovableAd) findViewById(R.id.spotBoardRemovableAd);

        mResultSource = new ResultSource(this);
        mResultSource.open();

        mCountDownTimer = new CountDownTimer(TimeUnit.MINUTES.toMillis(TimeSpanEnum.THREE.getValue())
                , TIME_PROMPT_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                Time time = new Time();
                time.set(millisUntilFinished);

                mSpotTitle.setText(String.format(getString(R.string.spot_board_title)
                        , time.format("%M:%S")));

            }

            @Override
            public void onFinish() {
                mStart.setVisibility(View.VISIBLE);
                mStart.setText(R.string.spot_button_restart);
                mRemovableAd.setVisibility(View.VISIBLE);
                mReview.setVisibility(View.VISIBLE);
                mSpotTitle.setText(String.format(getString(R.string.spot_board_result), mHappyCount));
                mSpotGrid.setVisibility(View.GONE);

                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                int month = Integer.valueOf(monthFormat.format(new Date()));
                int date = Integer.valueOf(dateFormat.format(new Date()));
                ResultData formerResult = mResultSource.getResultData(month, date);
                if (formerResult == null || mHappyCount > formerResult.getResult()) {
                    mResultSource.createResultData(month, date, String.valueOf(TimeSpanEnum.THREE), mHappyCount);
                }

            }
        };

        mSpotGrid = (GridView) findViewById(R.id.spotGrid);
        mSpotTitle = (TextView) findViewById(R.id.spotTitle);
        mStart = (Button) findViewById(R.id.spotStart);
        mReview = (Button) findViewById(R.id.spotReview);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemovableAd.setVisibility(View.GONE);
                mHappyCount = 0;
                mCountDownTimer.start();
                mSpotTitle.setText(R.string.spot_board_title);
                mSpotTitle.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);
                mSpotGrid.setVisibility(View.VISIBLE);
                mReview.setVisibility(View.GONE);
                mAddImageContainer.setVisibility(View.GONE);
            }
        });

        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SpotBoardActivity.this, ResultActivity.class));
            }
        });

        File sdSmileDir = new File(SDCARD_SPOT_SMILE_HAPPY);

        if (sdSmileDir.exists()) {
            mSpotBoardAdapter = new SpotBoardAdapter(SpotBoardActivity.this,
                    getResources().obtainTypedArray(R.array.angry_face_list),
                    getResources().obtainTypedArray(R.array.happy_face_list),
                    sdSmileDir.list());
        } else {
            mIsStorageAvailable = sdSmileDir.mkdirs();
            mSpotBoardAdapter = new SpotBoardAdapter(SpotBoardActivity.this,
                    getResources().obtainTypedArray(R.array.angry_face_list),
                    getResources().obtainTypedArray(R.array.happy_face_list));
        }

        mSpotGrid.setAdapter(mSpotBoardAdapter);
        mSpotGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mSpotBoardAdapter.getImageAngryPos()) {
                    mHappyCount++;
                    refreshSpotGrid();
                }
            }
        });

        mAddImageContainer = findViewById(R.id.addImageContainer);
        mManageMyImage = (Button) findViewById(R.id.manageMyImages);
        View addImageButtonGroup = findViewById(R.id.addImageButtonGroup);

        if(mIsStorageAvailable) {
            addImageButtonGroup.setVisibility(View.VISIBLE);
            Button addFromCamera = (Button) findViewById(R.id.addFromCamera);
            Button addFromGallery = (Button) findViewById(R.id.addFromGallery);
            TextView addTitle = (TextView) findViewById(R.id.addImageText);

            addTitle.setText(R.string.my_image_text);

            addFromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            addFromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            });

            mManageMyImage.setEnabled(sdSmileDir.list().length > 0);
            mManageMyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SpotBoardActivity.this, MyImageActivity.class));
                }
            });
        }

        mEasyRatingDialog = new EasyRatingDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mIsStorageAvailable) {
            File sdSmileDir = new File(SDCARD_SPOT_SMILE_HAPPY);
            mManageMyImage.setEnabled(sdSmileDir.list().length > 0);
        }

        mEasyRatingDialog.showIfNeeded();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mResultSource.open();
        mEasyRatingDialog.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mResultSource.close();
    }

    @Override
    public void onBackPressed() {
        mRemovableAd.setVisibility(View.GONE);
        if (mSpotGrid.isShown()) {
            mHappyCount = 0;
            mCountDownTimer.cancel();
            mSpotTitle.setVisibility(View.GONE);
            mStart.setVisibility(View.VISIBLE);
            mStart.setText(R.string.spot_button_restart);
            mSpotGrid.setVisibility(View.GONE);
            mReview.setVisibility(View.VISIBLE);
            mAddImageContainer.setVisibility(View.VISIBLE);
            mSpotBoardAdapter.clearHappyList();
            refreshSpotGrid();
        } else {
            super.onBackPressed();
        }

    }

    private void refreshSpotGrid() {
        mSpotBoardAdapter.setImgHappyPos();
        mSpotBoardAdapter.setAngryList();
        mSpotBoardAdapter.setHappyList();
        mSpotGrid.setAdapter(mSpotBoardAdapter);
        mSpotGrid.invalidateViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();
                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap croppedPhoto = extras.getParcelable("data");

                    File sdSmileDir = new File(SDCARD_SPOT_SMILE_HAPPY);
                    if (!sdSmileDir.exists()) {
                        sdSmileDir.mkdirs();
                    }

                    FileOutputStream out = null;
                    try {

                        String currentDateAndTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        out = new FileOutputStream(String.format(SDCARD_SPOT_SMILE_HAPPY + "%s.png", currentDateAndTime.toString()));
                        croppedPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mSpotBoardAdapter.setSdHappyImages(sdSmileDir.list());
                }

                break;

        }
    }

    private void doCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

}


