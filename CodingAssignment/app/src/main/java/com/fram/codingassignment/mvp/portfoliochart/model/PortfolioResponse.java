package com.fram.codingassignment.mvp.portfoliochart.model;

import com.fram.codingassignment.mvp.base.usecase.ResponseValue;

import java.util.List;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioResponse implements ResponseValue {

    private final List<Portfolio> portfolioList;

    public PortfolioResponse(List<Portfolio> portfolioList) {
        this.portfolioList = portfolioList;
    }

    public List<Portfolio> getPortfolioList() {
        return portfolioList;
    }
}
