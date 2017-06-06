package com.fram.codingassignment.mvp.portfoliochart;

import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fram.codingassignment.R;
import com.fram.codingassignment.chart.DayAxisValueFormatter;
import com.fram.codingassignment.chart.MyAxisValueFormatter;
import com.fram.codingassignment.chart.XYMarkerView;
import com.fram.codingassignment.mvp.base.BackHandledFragment;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.model.Nav;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.util.StringUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioChartFragmentTest1 extends BackHandledFragment implements IPortfolioChart.View,
        OnChartValueSelectedListener {

    @BindView(R.id.smooth_progress_bar)
    SmoothProgressBar vSmoothProgressBar;

    @BindView(R.id.chart)
    BarChart vChart;

    private IPortfolioChart.Presenter mPresenter;

    private Typeface mTfLight;

    private RectF mOnValueSelectedRectF = new RectF();

    public static PortfolioChartFragmentTest1 newInstance() {
        return new PortfolioChartFragmentTest1();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_portfolio_chart_test2, container, false);
        ButterKnife.bind(this, root);
        getPresenter().subscribe();

        initView();
        initData();

        return root;
    }

    private void initData() {
        String json = StringUtils.readJson(getActivity(), "data_json.json");
        mPresenter.readPortfolioData(json);
    }

    private void initView() {
        initFont();
        initChartView();
    }

    private void initFont(){
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
    }

    private void initChartView() {
        vChart.setOnChartValueSelectedListener(this);

        vChart.setDrawBarShadow(false);
        vChart.setDrawValueAboveBar(true);

        vChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        vChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        vChart.setPinchZoom(false);

        vChart.setDrawGridBackground(false);
        // vChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(vChart);

        XAxis xAxis = vChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = vChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = vChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = vChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(vChart); // For bounds control
        vChart.setMarker(mv); // Set the marker to the chart

        setData(12, 50);
    }

    private void setData(int count, float range) {
        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
            } else {
                yVals1.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (vChart.getData() != null &&
                vChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) vChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            vChart.getData().notifyDataChanged();
            vChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Portfolio Chart");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            vChart.setData(data);
        }
    }

    @Override
    public void onStop() {
        getPresenter().unsubscribe();
        super.onStop();
    }

    @Override
    public String getTagText() {
        return PortfolioChartFragmentTest1.class.toString();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onReadPortfolioDataError(int errorCode) {

    }

    @Override
    public void onReadPortfolioDataSuccess(List<Portfolio> portfolioList) {
        float sum = 0;
        int count = 0;
        for(Portfolio portfolio : portfolioList){
//            for(Nav nav : portfolio.getNavs()){
//                sum += nav.getAmount();
//                count ++;
//            }
        }
        float averageAmount = (sum / count);
        setData(portfolioList.size(), averageAmount);
    }

    @Override
    public void onFilterPortfolioError(int errorCode) {

    }

    @Override
    public void onFilterPortfolioSuccess(List<Portfolio> portfolioList) {

    }

    @Override
    public void showLoadingProgress(boolean isShow) {
        showSmoothProgressBar(vSmoothProgressBar, isShow);
    }

    @Override
    public void setPresenter(IPortfolioChart.Presenter presenter) {
        mPresenter = presenter;
    }

    private IPortfolioChart.Presenter getPresenter(){
        if (mPresenter == null) {
            mPresenter = new PortfolioChartPresenter(PortfolioChartFragmentTest1.this, UseCaseRxHandler.getInstance());
        }
        return mPresenter;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        vChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = vChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + vChart.getLowestVisibleX() + ", high: "
                        + vChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }
}
