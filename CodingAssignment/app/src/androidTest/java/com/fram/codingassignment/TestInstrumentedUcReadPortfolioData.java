package com.fram.codingassignment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcReadPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.fram.codingassignment.util.StringUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestInstrumentedUcReadPortfolioData {
    private static final String TAG = TestInstrumentedUcReadPortfolioData.class.toString();

//    @Test
//    public void useAppContext() throws Exception {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("com.fram.codingassignment", appContext.getPackageName());
//    }

    @Test
    public void testReadJsonData(){
        Context appContext = InstrumentationRegistry.getTargetContext();

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

                mUseCaseRxHandler.release();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError read portfolio data e = " + e.toString());

                mUseCaseRxHandler.release();
            }
        });

    }
}
