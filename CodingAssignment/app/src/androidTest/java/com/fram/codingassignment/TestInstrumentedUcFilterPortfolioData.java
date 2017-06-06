package com.fram.codingassignment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcFilterPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcReadPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.FilterPortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.fram.codingassignment.util.Config;
import com.fram.codingassignment.util.StringUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestInstrumentedUcFilterPortfolioData {

    private static final String TAG = TestInstrumentedUcFilterPortfolioData.class.toString();

    @Test
    public void testReadJsonData(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        final UcFilterPortfolioData uFilterPortfolioData = new UcFilterPortfolioData();
        UcReadPortfolioData uReadPortfolioData = new UcReadPortfolioData();
        final UseCaseRxHandler mUseCaseRxHandler = UseCaseRxHandler.getInstance();
        mUseCaseRxHandler.resume();

        String json = StringUtils.readJson(appContext, "data_json.json");
        assertEquals(json != null, true);

        PortfolioRequest requestValues = new PortfolioRequest(json);
        mUseCaseRxHandler.clear();
        mUseCaseRxHandler.execute(uReadPortfolioData, requestValues, new UseCase.UseCaseCallback<PortfolioResponse>() {
            @Override
            public void onSuccess(PortfolioResponse response) {
                Log.e(TAG, "onSuccess read portfolio data");
                assertEquals(response.getPortfolioList().size(), 3);

                FilterPortfolioRequest requestValues = new FilterPortfolioRequest(UcFilterPortfolioData.FilterMode.MONTHLY, response.getPortfolioList(), 0);
                mUseCaseRxHandler.clear();
                mUseCaseRxHandler.execute(uFilterPortfolioData, requestValues, new UseCase.UseCaseCallback<PortfolioResponse>() {
                    @Override
                    public void onSuccess(PortfolioResponse response) {
                        Log.e(TAG, "onSuccess filter portfolio data");
                        assertEquals(response.getPortfolioList().size() < 12, true);

                        mUseCaseRxHandler.release();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError filter portfolio data e = " + e.toString());

                        mUseCaseRxHandler.release();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError read portfolio data e = " + e.toString());

                mUseCaseRxHandler.release();
            }
        });
    }
}
