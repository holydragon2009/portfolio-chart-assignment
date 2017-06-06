package com.fram.codingassignment.mvp.portfoliochart.domain;

import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.portfoliochart.model.FilterPortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                int removeCount = 0;
                for(int x = 0; x < originPortfolioList.size(); x++){
                    HashMap<String, Float> navHm = new LinkedHashMap<>();
                    Iterator it = originPortfolioList.get(x).getNavHm().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Float> pair = (Map.Entry<String, Float>)it.next();
                        String currentMonthKey = "2017" + "-" + String.format("%02d", (currentMonth + 1));
                        if(pair.getKey().contains(currentMonthKey)){
                            navHm.put(pair.getKey(), pair.getValue());
                        }
//                        it.remove();
                    }
                    if(navHm.size() > 0){
                        filterPortfolioList.get(x - removeCount).setNavHm(navHm);
                    } else {
                        if(filterPortfolioList.size() > x) {
                            filterPortfolioList.remove(x - removeCount);
                            removeCount++;
                        }
                    }
                }
                break;
            case MONTHLY:
                for(int x = 0; x < originPortfolioList.size(); x++){
                    HashMap<String, Float> navHm = new LinkedHashMap<>();
                    for(int i = 0; i < 12; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH, i);

                        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        while(true) {
                            String key = cal.get(Calendar.YEAR) + "-" + (i + 1) + "-" + lastDayOfMonth;
                            if(originPortfolioList.get(x).getNavHm().containsKey(key)){
                                navHm.put(key, originPortfolioList.get(x).getNavHm().get(key));
                                break;
                            } else {
                                if(lastDayOfMonth > 1){
                                    lastDayOfMonth--;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    Log.e(TAG, "filter monthly size = " + navHm.size());
                    filterPortfolioList.get(x).setNavHm(navHm);
                }
                break;
            case QUARTERLY:
                for(int x = 0; x < originPortfolioList.size(); x++){
                    HashMap<String, Float> navHm = new LinkedHashMap<>();
                    for(int i = 0; i < 12; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH, i);

                        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        while(true) {
                            String key = cal.get(Calendar.YEAR) + "-" + (i + 1) + "-" + lastDayOfMonth;
                            if(originPortfolioList.get(x).getNavHm().containsKey(key)){
                                navHm.put(key, originPortfolioList.get(x).getNavHm().get(key));
                                break;
                            } else {
                                if(lastDayOfMonth > 1){
                                    lastDayOfMonth--;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    Log.e(TAG, "filter quarterly size = " + navHm.size());
                    filterPortfolioList.get(x).setNavHm(navHm);
                }
                break;
            default:
                break;
        }
        return new PortfolioResponse(filterPortfolioList);
    }

}
