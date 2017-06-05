package com.fram.codingassignment.mvp.portfoliochart;


import com.fram.codingassignment.mvp.base.BasePresenter;
import com.fram.codingassignment.mvp.base.BaseView;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcFilterPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;

import java.util.List;

/**
 * Created by thaile on 3/2/17.
 */

public interface IPortfolioChart {

    interface View extends BaseView<Presenter> {
        void onReadPortfolioDataError(int errorCode);
        void onReadPortfolioDataSuccess(List<Portfolio> portfolioList);
        void onFilterPortfolioError(int errorCode);
        void onFilterPortfolioSuccess(List<Portfolio> portfolioList);
        void showLoadingProgress(boolean isShow);
    }

    interface Presenter extends BasePresenter {
        void readPortfolioData(String json);
        void filterPortfolioData(UcFilterPortfolioData.FilterMode filterMode, List<Portfolio> originPortfolioList, int currentMonth);
    }

}
