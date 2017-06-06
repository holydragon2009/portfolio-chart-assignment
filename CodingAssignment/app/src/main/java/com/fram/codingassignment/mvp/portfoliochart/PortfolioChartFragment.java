package com.fram.codingassignment.mvp.portfoliochart;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fram.codingassignment.R;
import com.fram.codingassignment.chart.MyMarkerView;
import com.fram.codingassignment.mvp.base.BackHandledFragment;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.domain.UcFilterPortfolioData;
import com.fram.codingassignment.mvp.portfoliochart.model.Nav;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.util.Config;
import com.fram.codingassignment.util.StringUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioChartFragment extends BackHandledFragment implements IPortfolioChart.View,
        OnChartGestureListener, OnChartValueSelectedListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = PortfolioChartFragment.class.toString();

    @BindView(R.id.smooth_progress_bar)
    SmoothProgressBar vSmoothProgressBar;

    @BindView(R.id.chart)
    LineChart vChart;

    @BindView(R.id.mode_picker)
    Button vModePicker;

    @BindView(R.id.month_picker)
    Button vMonthPicker;

    @BindView(R.id.toolbar_title)
    TextView vToolbarTitle;

    private IPortfolioChart.Presenter mPresenter;

    private List<Portfolio> originPortfolioList = new ArrayList<>();
    private List<Portfolio> filterPortfolioList = new ArrayList<>();

    private UcFilterPortfolioData.FilterMode currentFilterMode = UcFilterPortfolioData.FilterMode.DAILY;
    private int currentMonthIndex = 0;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    public static PortfolioChartFragment newInstance() {
        return new PortfolioChartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_portfolio_chart, container, false);
        ButterKnife.bind(this, root);
        getPresenter().subscribe();

        setupFirebaseDb();
        readData();

        return root;
    }

    private void setupFirebaseDb(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Config.KEY_PORTFOLIOS);
        mFirebaseInstance.getReference(Config.KEY_APP_TITLE).setValue(getString(R.string.portfolio_chart_title));
        mFirebaseInstance.getReference(Config.KEY_APP_TITLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");
                String appTitle = dataSnapshot.getValue(String.class);
                vToolbarTitle.setText(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }

    private void readData() {
        String json = StringUtils.readJson(getActivity(), "data_json.json");
        mPresenter.readPortfolioData(json);
    }

    private void drawChartView(List<Portfolio> portfolioList) {
        vChart.setOnChartGestureListener(this);
        vChart.setOnChartValueSelectedListener(this);
        vChart.setDrawGridBackground(false);

        vChart.getDescription().setEnabled(false);
        vChart.setTouchEnabled(true);
        vChart.setDragEnabled(true);
        vChart.setScaleEnabled(true);
        vChart.setPinchZoom(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(vChart); // For bounds control
        vChart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = vChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        float max = 0;
        for(Portfolio portfolio : portfolioList){
            Iterator it = portfolio.getNavHm().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Float> pair = (Map.Entry<String, Float>)it.next();
                if(pair.getValue() > max){
                    max = pair.getValue();
                }
//                it.remove();
            }
        }
        Log.d(TAG, "max amount = " + max);

        YAxis leftAxis = vChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        vChart.getAxisRight().setEnabled(false);

        // add data
        setData(UcFilterPortfolioData.FilterMode.DAILY, portfolioList);
//        // redraw
//        vChart.invalidate();

        vChart.animateX(2500);
        //vChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = vChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData(UcFilterPortfolioData.FilterMode filterMode, List<Portfolio> portfolioList) {
        int count = 31;
        switch (filterMode){
            case DAILY:
                count = 31;
                break;
            case MONTHLY:
                count = 12;
                break;
            case QUARTERLY:
                count = 4;
                break;
            default:
                break;
        }

        HashMap<Integer, ArrayList<Entry>> valueHashMap = new HashMap<>();

        for(int x = 0; x < portfolioList.size(); x++) {
            ArrayList<Entry> values = new ArrayList<Entry>();
            if(portfolioList.get(x).getNavHm().size() > 0) {
                Iterator it = portfolioList.get(x).getNavHm().entrySet().iterator();
                int i = 0;
                while (it.hasNext() && i < count) {
                    Map.Entry<String, Float> pair = (Map.Entry<String, Float>)it.next();
                    float val = pair.getValue();
                    values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
//                    it.remove();
                    i++;
                }
                valueHashMap.put(x, values);
            }
        }

//        List<LineDataSet> dataSetList = new ArrayList<>();
//        if (vChart.getData() != null &&
//                vChart.getData().getDataSetCount() > 0) {
//            for(int x = 0; x < originPortfolioList.size(); x++){
//                LineDataSet set = (LineDataSet)vChart.getData().getDataSetByIndex(x);
//                set.setValues(valueHashMap.get(x));
//                vChart.getData().notifyDataChanged();
//                vChart.notifyDataSetChanged();
//
//                dataSetList.add(x, set);
//            }
//        } else {
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            for(int x = 0; x < portfolioList.size(); x++) {
                // create a dataset and give it a type
                String title = portfolioList.get(x).getPortfolioId().length() > 10 ?
                        "..." + portfolioList.get(x).getPortfolioId().substring(portfolioList.get(x).getPortfolioId().length() - 5)
                        : portfolioList.get(x).getPortfolioId();
                LineDataSet set = new LineDataSet(valueHashMap.get(x), title);

                set.setDrawIcons(false);

                // set the line to be drawn like this "- - - - - -"
                set.enableDashedLine(10f, 5f, 0f);
                set.enableDashedHighlightLine(10f, 5f, 0f);
                if(x == 1){
                    set.setColor(Color.BLUE);
                    if (Utils.getSDKInt() >= 18) {
                        // fill drawable only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_blue);
                        set.setFillDrawable(drawable);
                    } else {
                        set.setFillColor(Color.BLUE);
                    }
                } else if(x == 2){
                    set.setColor(Color.GREEN);
                    if (Utils.getSDKInt() >= 18) {
                        // fill drawable only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_green);
                        set.setFillDrawable(drawable);
                    } else {
                        set.setFillColor(Color.GREEN);
                    }
                } else {
                    set.setColor(Color.RED);
                    if (Utils.getSDKInt() >= 18) {
                        // fill drawable only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
                        set.setFillDrawable(drawable);
                    } else {
                        set.setFillColor(Color.RED);
                    }
                }
                set.setCircleColor(Color.WHITE);
                set.setLineWidth(1f);
                set.setCircleRadius(3f);
                set.setDrawCircleHole(false);
                set.setValueTextSize(9f);
                set.setDrawFilled(true);
                set.setFormLineWidth(1f);
                set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set.setFormSize(15.f);

                dataSets.add(set); // add the datasets
            }

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            vChart.setData(data);
//        }
    }

    @Override
    public void onStop() {
        getPresenter().unsubscribe();
        super.onStop();
    }

    @Override
    public String getTagText() {
        return PortfolioChartFragment.class.toString();
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
        this.originPortfolioList = portfolioList;
        mPresenter.filterPortfolioData(this.currentFilterMode, this.originPortfolioList, this.currentMonthIndex);
        mPresenter.syncPortfolioData(this.originPortfolioList, this.mFirebaseDatabase);
    }

    @Override
    public void onFilterPortfolioError(int errorCode) {

    }

    @Override
    public void onFilterPortfolioSuccess(List<Portfolio> portfolioList) {
        filterPortfolioList.clear();
        filterPortfolioList = portfolioList;
        drawChartView(filterPortfolioList);
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
            mPresenter = new PortfolioChartPresenter(PortfolioChartFragment.this, UseCaseRxHandler.getInstance());
        }
        return mPresenter;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            vChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + vChart.getLowestVisibleX() + ", high: " + vChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + vChart.getXChartMin() + ", xmax: " + vChart.getXChartMax() + ", ymin: " + vChart.getYChartMin() + ", ymax: " + vChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @OnClick({R.id.mode_picker, R.id.month_picker})
    public void onDatePickerClick(View view){
        if(view.getId() == R.id.mode_picker){
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.select_filter)
                    .items(R.array.filter_mode_array)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which){
                                case 0:
                                    currentFilterMode = UcFilterPortfolioData.FilterMode.DAILY;
                                    vMonthPicker.setVisibility(View.VISIBLE);
                                    vModePicker.setText(getResources().getStringArray(R.array.filter_mode_array)[0]
                                                        + " - " + getResources().getStringArray(R.array.month_array)[currentMonthIndex]);
                                    break;
                                case 1:
                                    currentFilterMode = UcFilterPortfolioData.FilterMode.MONTHLY;
                                    vMonthPicker.setVisibility(View.GONE);
                                    vModePicker.setText(getResources().getStringArray(R.array.filter_mode_array)[1]);
                                    break;
                                case 2:
                                    currentFilterMode = UcFilterPortfolioData.FilterMode.QUARTERLY;
                                    vMonthPicker.setVisibility(View.GONE);
                                    vModePicker.setText(getResources().getStringArray(R.array.filter_mode_array)[2]);
                                    break;
                                default:
                                    break;
                            }
                            mPresenter.filterPortfolioData(currentFilterMode, originPortfolioList, currentMonthIndex);
                        }
                    })
                    .show();
        } else if(view.getId() == R.id.month_picker){
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.select_month)
                    .items(R.array.month_array)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            currentMonthIndex = which;
                            vModePicker.setText(getResources().getStringArray(R.array.filter_mode_array)[0]
                                    + " - " + getResources().getStringArray(R.array.month_array)[currentMonthIndex]);
                            mPresenter.filterPortfolioData(currentFilterMode, originPortfolioList, currentMonthIndex);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        vModePicker.setText("Selected date : " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//        for(int i = 0; i < originPortfolioList.size(); i++){
//            int m = CollectionUtil.interpolationSearch(originPortfolioList.get(i).getNavs(), monthOfYear, false);
//            int d = CollectionUtil.interpolationSearch(originPortfolioList.get(i).getNavs(), dayOfMonth, true);
//
//        }
    }

}
