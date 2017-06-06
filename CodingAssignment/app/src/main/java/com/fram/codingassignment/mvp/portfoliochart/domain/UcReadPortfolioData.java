package com.fram.codingassignment.mvp.portfoliochart.domain;

import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioDeserializer;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.fram.codingassignment.util.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;


/**
 * Created by thaile on 6/4/17.
 */

public class UcReadPortfolioData extends UseCase<PortfolioRequest, PortfolioResponse> {

    private static final String TAG = UcReadPortfolioData.class.toString();

    @Override
    protected Observable<PortfolioResponse> getObservable(PortfolioRequest requestValues) {
        return null;
    }

    @Override
    protected PortfolioResponse executeUseCase(PortfolioRequest requestValues) {
        PortfolioResponse portfolioResponse = readJsonData(requestValues.getJson());
        sortIndexData(portfolioResponse);
        calculateSumEachDay(portfolioResponse);
        return portfolioResponse;
    }

    private PortfolioResponse readJsonData(String json){
        try {
            Type collectionType = new TypeToken<List<Portfolio>>(){}.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(collectionType, new PortfolioDeserializer());
            Gson gson = gsonBuilder.create();
            return new PortfolioResponse(gson.<List<Portfolio>>fromJson(json, new TypeToken<List<Portfolio>>(){}.getType()));
        } catch (Exception e) {
            Log.e(TAG, "readJsonData error = " + e.toString());
        }
        return null;
    }

    private <T> List<T> toList(String json, Class<T> clazz) {
        if (null == json) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<T>(){}.getType());
    }

    private void sortIndexData(PortfolioResponse portfolioResponse){
        for(int i = 0; i < portfolioResponse.getPortfolioList().size(); i ++){
            HashMap<String, Float> navHm = new LinkedHashMap<>();
            for(int j = 0; j < portfolioResponse.getPortfolioList().get(i).getNavs().size(); j++){
                String date = portfolioResponse.getPortfolioList().get(i).getNavs().get(j).getDate();
                Float amount = portfolioResponse.getPortfolioList().get(i).getNavs().get(j).getAmount();
                navHm.put(date, amount);

//                Calendar cal = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat(Config.DATETIME_FORMAT, Locale.ENGLISH);
//                try {
//                    cal.setTime(sdf.parse(date));
//                    //Set month
//                    portfolioResponse.getPortfolioList().get(i).getNavs().get(j).setMonth(cal.get(Calendar.MONTH));
//                    //Set day
//                    portfolioResponse.getPortfolioList().get(i).getNavs().get(j).setDay(cal.get(Calendar.DAY_OF_MONTH));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
            }
            portfolioResponse.getPortfolioList().get(i).setNavHm(navHm);
        }
    }

    private void calculateSumEachDay(PortfolioResponse portfolioResponse){
        List<Portfolio>  portfolioList = portfolioResponse.getPortfolioList();
        SimpleDateFormat formatter = new SimpleDateFormat(Config.DATETIME_FORMAT, Locale.ENGLISH);
        try {
            Date startDate = formatter.parse("2017-01-01");
            Date endDate = formatter.parse("2017-12-31");

            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            Portfolio sumPortfolio = new Portfolio();
            sumPortfolio.setPortfolioId("SUM");
            HashMap<String, Float> navHm = new HashMap<>();
            for (Calendar date = start; start.before(end); start.add(Calendar.DATE, 1), date = start) {
                float sum = 0;
                String key = date.get(Calendar.YEAR) + "-"
                        + String.format("%02d", (date.get(Calendar.MONTH) + 1)) + "-"
                        + String.format("%02d", (date.get(Calendar.DAY_OF_MONTH) + 1));
                for(int i = 0; i < portfolioList.size(); i++){
                    if(portfolioList.get(i).getNavHm().containsKey(key)){
                        sum += portfolioList.get(i).getNavHm().get(key);
                    }
                }
                if(sum > 0){
                    navHm.put(key, sum);
                }
            }
            if(navHm.size() > 0) {
                sumPortfolio.setNavHm(navHm);
                portfolioList.add(sumPortfolio);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
