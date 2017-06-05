package com.fram.codingassignment.mvp.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.fram.codingassignment.util.Config;

import java.util.HashMap;

/**
 * Created by thaile on 3/8/17.
 */

public abstract class BackHandledFragment extends Fragment {

    public static final int ANIMATION_DELAY = 100;
    private Interpolator interpolator;

    protected BackHandlerInterface backHandlerInterface;
    public abstract String getTagText();
    public abstract boolean onBackPressed();

    protected HashMap<View, Long> mPreventMultiClicksMap;
    protected Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity()  instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
        mPreventMultiClicksMap = new HashMap<>();
        interpolator = AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear_out_slow_in);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Mark this fragment as the selected Fragment.
        backHandlerInterface.setSelectedFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    protected boolean isNetworkAvailable(boolean isDialogShow){
//        boolean isNetworkAvailable = SavedCache.getInstance(getActivity()).isNetworkAvailable();
//        if(!isNetworkAvailable && isDialogShow){
//            DialogHelper.showBasicDismissDialog(getActivity(), getString(R.string.error), ErrorType.E_NO_NETWORK.resource(getActivity()));
//        }
//        return isNetworkAvailable;
//    }

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
        ((BaseActivity) getActivity()).showSmoothProgressBar(isShow);
    }

    protected void showSmoothProgressBar(View smoothProgressBar, boolean isShow){
        if(isShow){
            smoothProgressBar.setVisibility(View.VISIBLE);
        } else {
            smoothProgressBar.setVisibility(View.GONE);
        }
    }

    public interface BackHandlerInterface {
        public void setSelectedFragment(BackHandledFragment backHandledFragment);
    }

}