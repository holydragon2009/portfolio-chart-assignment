package com.fram.codingassignment.mvp.portfoliochart.model;

import com.fram.codingassignment.mvp.base.usecase.ResponseValue;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by thaile on 6/4/17.
 */

@IgnoreExtraProperties
public class Portfolio implements ResponseValue {

    @SerializedName("portfolioId")
    private String portfolioId;

    @SerializedName("navs")
    private List<Nav> navs;

    private Map<String, Float> navHm;

    public Portfolio() {
    }

    public Portfolio(String portfolioId, List<Nav> navs) {
        this.portfolioId = portfolioId;
        this.navs = navs;
    }

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

    public Map<String, Float> getNavHm() {
        return navHm;
    }

    public void setNavHm(Map<String, Float> navHm) {
        this.navHm = navHm;
    }
}
