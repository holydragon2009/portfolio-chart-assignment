package com.fram.codingassignment.mvp.portfoliochart;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcFilterPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcReadPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.FilterPortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.fram.codingassignment.util.Config;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioChartPresenter implements IPortfolioChart.Presenter {

    private static final String TAG = PortfolioChartPresenter.class.toString();

    private IPortfolioChart.View mPortfolioChartView;
    private UcReadPortfolioData uReadPortfolioData;
    private UcFilterPortfolioData uFilterPortfolioData;
    private UseCaseRxHandler mUseCaseRxHandler;

    public PortfolioChartPresenter(@NonNull IPortfolioChart.View portfolioChartView, UseCaseRxHandler useCaseRxHandler){
        mUseCaseRxHandler = useCaseRxHandler;
        mPortfolioChartView = portfolioChartView;
        uReadPortfolioData = new UcReadPortfolioData();
        uFilterPortfolioData = new UcFilterPortfolioData();

        mPortfolioChartView.setPresenter(this);
    }

    @Override
    public void readPortfolioData(String json) {
        mPortfolioChartView.showLoadingProgress(true);
        PortfolioRequest requestValues = new PortfolioRequest(json);
        mUseCaseRxHandler.clear();
        mUseCaseRxHandler.execute(uReadPortfolioData, requestValues, new UseCase.UseCaseCallback<PortfolioResponse>() {
            @Override
            public void onSuccess(PortfolioResponse response) {
                Log.e(TAG, "onSuccess read portfolio data");
                mPortfolioChartView.showLoadingProgress(false);
                if(response.getPortfolioList() != null){
                    mPortfolioChartView.onReadPortfolioDataSuccess(response.getPortfolioList());
                } else {
                    mPortfolioChartView.onReadPortfolioDataError(Config.ERROR_CODE_DEFAULT);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError read portfolio data e = " + e.toString());
                mPortfolioChartView.showLoadingProgress(false);
                mPortfolioChartView.onReadPortfolioDataError(Config.ERROR_CODE_DEFAULT);
            }
        });
    }

    @Override
    public void filterPortfolioData(UcFilterPortfolioData.FilterMode filterMode, List<Portfolio> originPortfolioList, int currentMonth) {
        mPortfolioChartView.showLoadingProgress(true);
        FilterPortfolioRequest requestValues = new FilterPortfolioRequest(filterMode, originPortfolioList, currentMonth);
        mUseCaseRxHandler.clear();
        mUseCaseRxHandler.execute(uFilterPortfolioData, requestValues, new UseCase.UseCaseCallback<PortfolioResponse>() {
            @Override
            public void onSuccess(PortfolioResponse response) {
                Log.e(TAG, "onSuccess filter portfolio data");
                mPortfolioChartView.showLoadingProgress(false);
                if(response.getPortfolioList() != null){
                    mPortfolioChartView.onFilterPortfolioSuccess(response.getPortfolioList());
                } else {
                    mPortfolioChartView.onFilterPortfolioError(Config.ERROR_CODE_DEFAULT);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError filter portfolio data e = " + e.toString());
                mPortfolioChartView.showLoadingProgress(false);
                mPortfolioChartView.onFilterPortfolioError(Config.ERROR_CODE_DEFAULT);
            }
        });
    }

    @Override
    public void syncPortfolioData(List<Portfolio> portfolioList, DatabaseReference firebaseDatabase) {
        for(int i = 0; i < portfolioList.size(); i++){
            Portfolio portfolio = portfolioList.get(i);
            if(TextUtils.isEmpty(portfolio.getPortfolioId())){
                portfolio.setPortfolioId(firebaseDatabase.child(Config.KEY_PORTFOLIOS).push().getKey());
            }
            firebaseDatabase.child(Config.KEY_PORTFOLIOS).child(portfolio.getPortfolioId()).setValue(portfolio);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void subscribe() {
        mUseCaseRxHandler.resume();
    }

    @Override
    public void unsubscribe() {
        mUseCaseRxHandler.release();
    }
}
