package com.fram.codingassignment.mvp.base;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.fram.codingassignment.R;
import com.fram.codingassignment.util.Config;

import java.util.HashMap;

/**
 * Created by thaile on 3/3/17.
 */

public class BaseActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    protected HashMap<View, Long> mPreventMultiClicksMap;
    private Interpolator interpolator;
    private static final int ANIMATION_DELAY = 100;
    protected Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mPreventMultiClicksMap = new HashMap<>();
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {

    }

    protected boolean isClickable(View view){
        if(mPreventMultiClicksMap.containsKey(view)){
            // Preventing multiple clicks, using threshold of 1 second
            if (SystemClock.elapsedRealtime() - mPreventMultiClicksMap.get(view) < Config.CLICKABLE_THRESHOLD_DURATION) {
                return false;
            }
        }
        mPreventMultiClicksMap.put(view, SystemClock.elapsedRealtime());
        return true;
    }

    protected boolean isClickable(View view, long duration){
        if(mPreventMultiClicksMap.containsKey(view)){
            // Preventing multiple clicks, using threshold of 1 second
            if (SystemClock.elapsedRealtime() - mPreventMultiClicksMap.get(view) < duration) {
                return false;
            }
        }
        mPreventMultiClicksMap.put(view, SystemClock.elapsedRealtime());
        return true;
    }

    protected void showSmoothProgressBar(boolean isShow){
        if(findViewById(R.id.smooth_progress_bar) != null){
            if(isShow){
                findViewById(R.id.smooth_progress_bar).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.smooth_progress_bar).setVisibility(View.GONE);
            }
        }
    }

}
