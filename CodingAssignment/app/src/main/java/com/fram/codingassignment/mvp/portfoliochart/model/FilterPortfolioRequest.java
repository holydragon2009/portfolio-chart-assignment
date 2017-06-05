package com.fram.codingassignment.mvp.portfoliochart.model;

import com.fram.codingassignment.mvp.base.usecase.RequestValues;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcFilterPortfolioData;

import java.util.List;

/**
 * Created by thaile on 6/4/17.
 */

public class FilterPortfolioRequest implements RequestValues {

    private final UcFilterPortfolioData.FilterMode filterMode;

    private final List<Portfolio> originData;

    private final int currentMonth;

    public FilterPortfolioRequest(UcFilterPortfolioData.FilterMode filterMode, List<Portfolio> originData, int currentMonth) {
        this.filterMode = filterMode;
        this.originData = originData;
        this.currentMonth = currentMonth;
    }

    public UcFilterPortfolioData.FilterMode getFilterMode() {
        return filterMode;
    }

    public List<Portfolio> getOriginData() {
        return originData;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }
}
