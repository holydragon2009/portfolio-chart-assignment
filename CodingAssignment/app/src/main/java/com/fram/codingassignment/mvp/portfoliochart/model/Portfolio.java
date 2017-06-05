package com.fram.codingassignment.mvp.portfoliochart.model;

import com.fram.codingassignment.mvp.base.usecase.ResponseValue;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thaile on 6/4/17.
 */

public class Portfolio implements ResponseValue {

    @SerializedName("portfolioId")
    private String portfolioId;

    @SerializedName("navs")
    private List<Nav> navs;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public List<Nav> getNavs() {
        return navs;
    }

    public void setNavs(List<Nav> navs) {
        this.navs = navs;
    }

}
