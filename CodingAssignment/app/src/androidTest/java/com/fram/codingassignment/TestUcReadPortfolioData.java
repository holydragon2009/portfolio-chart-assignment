package com.fram.codingassignment;

import android.content.Context;
import android.util.Log;

import com.fram.codingassignment.mvp.base.usecase.UseCase;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcReadPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioRequest;
import com.fram.codingassignment.mvp.portfoliochart.model.PortfolioResponse;
import com.fram.codingassignment.util.StringUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;


/**
 * Created by thaile on 6/5/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class TestUcReadPortfolioData {

    private static final String TAG = TestUcReadPortfolioData.class.toString();

    @Mock
    Context mMockContext;

    public TestUcReadPortfolioData() {
    }

    @Test
    public void testReadJsonData(){
        UcReadPortfolioData uReadPortfolioData = new UcReadPortfolioData();
        final UseCaseRxHandler mUseCaseRxHandler = UseCaseRxHandler.getInstance();
        mUseCaseRxHandler.resume();

        String json = StringUtils.readJson(mMockContext, "data_json.json");
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
