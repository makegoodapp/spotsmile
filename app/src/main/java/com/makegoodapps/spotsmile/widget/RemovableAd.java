package com.makegoodapps.spotsmile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makegoodapps.spotsmile.R;

/**
 * Created by WillsPC on 2014/12/29.
 */
public class RemovableAd extends LinearLayout {

    private ImageView mRemoveAd;

    public RemovableAd(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        inflate(getContext(), R.layout.removable_ad, this);
        mRemoveAd = (ImageView)findViewById(R.id.removeAd);

        launchAdView();
        setRemoveAd();
    }

    private void launchAdView(){
    }

    private void setRemoveAd() {
        mRemoveAd = (ImageView)findViewById(R.id.removeAd);
        mRemoveAd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                View adBody = findViewById(R.id.removeAdBody);
                adBody.setVisibility(GONE);
            }
        });
    }

}
