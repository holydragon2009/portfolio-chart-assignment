package com.fram.codingassignment.mvp.portfoliochart.domain;

import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioDeserializer;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            for(int j = 0; j < portfolioResponse.getPortfolioList().get(i).getNavs().size(); j++){
                String date = portfolioResponse.getPortfolioList().get(i).getNavs().get(j).getDate();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    cal.setTime(sdf.parse(date));
                    //Set month
                    portfolioResponse.getPortfolioList().get(i).getNavs().get(j).setMonth(cal.get(Calendar.MONTH));
                    //Set day
                    portfolioResponse.getPortfolioList().get(i).getNavs().get(j).setDay(cal.get(Calendar.DAY_OF_MONTH));
//                    Log.e(TAG, "sortIndexData m = " + portfolioResponse.getPortfolioList().get(i).getNavs().get(j).getMonth()
//                    + " - d = " + portfolioResponse.getPortfolioList().get(i).getNavs().get(j).getDay());

//                    //Set hash
//                    try {
//                        portfolioResponse.getPortfolioList().get(i).getNavs().get(j)
//                                .setHash(EncryptionUtils.getSha1(
//                                        portfolioResponse.getPortfolioList().get(i).getPortfolioId() + "-" +
//                                        cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH)));
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    //Set timestamp
//                    portfolioResponse.getPortfolioList().get(i).getNavs().get(j).setTimestamp(cal.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
