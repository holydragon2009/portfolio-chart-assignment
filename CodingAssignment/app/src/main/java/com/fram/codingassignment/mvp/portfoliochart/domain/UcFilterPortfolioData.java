package com.fram.codingassignment.mvp.portfoliochart.domain;

import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.portfoliochart.model.FilterPortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.Nav;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;


/**
 * Created by thaile on 6/4/17.
 */

public class UcFilterPortfolioData extends UseCase<FilterPortfolioRequest, PortfolioResponse> {

    private static final String TAG = UcFilterPortfolioData.class.toString();

    public enum FilterMode {
        DAILY, MONTHLY, QUARTERLY
    }

    @Override
    protected Observable<PortfolioResponse> getObservable(FilterPortfolioRequest requestValues) {
        return null;
    }

    @Override
    protected PortfolioResponse executeUseCase(FilterPortfolioRequest requestValues) {
        return filterData(requestValues.getFilterMode(), requestValues.getOriginData(), requestValues.getCurrentMonth());
    }

    private PortfolioResponse filterData(UcFilterPortfolioData.FilterMode filterMode, List<Portfolio> originPortfolioList, int currentMonth){
        List<Portfolio> filterPortfolioList = new ArrayList<>();
        filterPortfolioList.clear();
        filterPortfolioList = new ArrayList<>();
        for(int x = 0; x < originPortfolioList.size(); x++){
            Portfolio port = new Portfolio();
            port.setPortfolioId(originPortfolioList.get(x).getPortfolioId());
            filterPortfolioList.add(port);
        }
        switch (filterMode){
            case DAILY:
                for(int x = 0; x < originPortfolioList.size(); x++){
                    List<Nav> navs = new ArrayList<>();
                    for(int i = 0; i < originPortfolioList.get(x).getNavs().size(); i++){
                        if(originPortfolioList.get(x).getNavs().get(i).getMonth() == currentMonth) {
                            navs.add(originPortfolioList.get(x).getNavs().get(i));
                        }
                    }
                    if(navs.size() > 0){
                        filterPortfolioList.get(x).setNavs(navs);
                    } else {
                        filterPortfolioList.remove(x);
                    }
                }
                break;
            case MONTHLY:
                for(int x = 0; x < originPortfolioList.size(); x++){
                    List<Nav> navs = new ArrayList<>();
                    for(int i = 0; i < 12; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH, i);

                        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        boolean isAvailable = false;
                        while(!isAvailable){
                            for(int j = 0; j < originPortfolioList.get(x).getNavs().size(); j++){
                                if(originPortfolioList.get(x).getNavs().get(j).getDate().equals(cal.get(Calendar.YEAR) + "-"
                                        + (i + 1) + "-" + lastDayOfMonth)){
                                    navs.add(originPortfolioList.get(x).getNavs().get(j));
                                    isAvailable = true;
                                    break;
                                }
                            }
                            if(lastDayOfMonth > 1){
                                lastDayOfMonth--;
                            } else {
                                break;
                            }
                        }
                    }
                    Log.e(TAG, "filter monthly size = " + navs.size());
                    filterPortfolioList.get(x).setNavs(navs);
                }
                break;
            case QUARTERLY:
                for(int x = 0; x < originPortfolioList.size(); x++){
                    List<Nav> navs = new ArrayList<>();
                    for(int i = 0; i < 12; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH, i);

                        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        boolean isAvailable = false;
                        while(!isAvailable){
                            for(int j = 0; j < originPortfolioList.get(x).getNavs().size(); j++){
                                if(originPortfolioList.get(x).getNavs().get(j).getDate().equals(cal.get(Calendar.YEAR) + "-"
                                        + (i + 1) + "-" + lastDayOfMonth)){
                                    navs.add(originPortfolioList.get(x).getNavs().get(j));
                                    isAvailable = true;
                                    break;
                                }
                            }
                            if(lastDayOfMonth > 1){
                                lastDayOfMonth--;
                            } else {
                                break;
                            }
                        }
                    }
                    Log.e(TAG, "filter quarterly size = " + navs.size());
                    filterPortfolioList.get(x).setNavs(navs);
                }
                break;
            default:
                break;
        }
        return new PortfolioResponse(filterPortfolioList);
    }

}
