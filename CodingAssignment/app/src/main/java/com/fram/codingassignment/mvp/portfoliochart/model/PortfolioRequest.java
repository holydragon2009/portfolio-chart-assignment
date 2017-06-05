package com.fram.codingassignment.mvp.portfoliochart.model;

import com.fram.codingassignment.mvp.base.usecase.RequestValues;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioRequest implements RequestValues {

    private final String json;

    public PortfolioRequest(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
