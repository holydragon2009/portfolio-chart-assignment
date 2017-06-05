package com.fram.codingassignment.mvp.portfoliochart;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fram.codingassignment.R;
import com.fram.codingassignment.mvp.base.BackHandledFragment;
import com.fram.codingassignment.mvp.base.BaseActivity;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.util.ActivityUtils;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioChartActivity extends BaseActivity {

    private IPortfolioChart.Presenter mPresenter;
    private BackHandledFragment mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_chart);

        mView = (PortfolioChartFragmentTest2) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mView == null) {
            mView = PortfolioChartFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mView, R.id.contentFrame);
        }

        mPresenter = new PortfolioChartPresenter((PortfolioChartFragment) mView,
                UseCaseRxHandler.getInstance());
    }

    @Override
    public void onBackPressed() {
        if (mView == null || !mView.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        mView = backHandledFragment;
    }
}


