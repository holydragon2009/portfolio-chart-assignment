package com.fram.codingassignment.mvp.portfoliochart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fram.codingassignment.R;
import com.fram.codingassignment.mvp.base.BackHandledFragment;
import com.fram.codingassignment.mvp.base.usecase.UseCaseRxHandler;
import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.fram.codingassignment.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioChartFragmentTest2 extends BackHandledFragment implements IPortfolioChart.View {

    @BindView(R.id.smooth_progress_bar)
    SmoothProgressBar vSmoothProgressBar;

    @BindView(R.id.chart_top)
    LineChartView vChartTop;

    @BindView(R.id.chart_bottom)
    ColumnChartView vChartBot;

    @BindView(R.id.chart_preview)
    PreviewLineChartView vChartPreview;

    private IPortfolioChart.Presenter mPresenter;

    public final static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec",};

    public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};

    private LineChartData lineData;
    private LineChartData previewData;

    private ColumnChartData columnData;

    private List<Portfolio> portfolioList;

    public static PortfolioChartFragmentTest2 newInstance() {
        return new PortfolioChartFragmentTest2();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_portfolio_chart, container, false);
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
//        initChartView();
    }

    private void initChartView() {
        // Generate and set data for line chart
        generateInitialLineData();

        generateColumnData();

        generateLineData(ChartUtils.COLOR_VIOLET, 50);

    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(vChartTop.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            vChartPreview.setCurrentViewportWithAnimation(tempViewport);
        } else {
            vChartPreview.setCurrentViewport(tempViewport);
        }
        vChartPreview.setZoomType(ZoomType.HORIZONTAL);
    }

    private void previewXY() {
        // Better to not modify viewport of any chart directly so create a copy.
        Viewport tempViewport = new Viewport(vChartTop.getMaximumViewport());
        // Make temp viewport smaller.
        float dx = tempViewport.width() / 4;
        float dy = tempViewport.height() / 4;
        tempViewport.inset(dx, dy);
        vChartPreview.setCurrentViewportWithAnimation(tempViewport);
    }

    @Override
    public void onStop() {
        getPresenter().unsubscribe();
        super.onStop();
    }

    @Override
    public String getTagText() {
        return PortfolioChartFragmentTest2.class.toString();
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
//        double sum = 0;
//        int count = 0;
//        for (Portfolio portfolio : portfolioList) {
//            for (Nav nav : portfolio.getNavs()) {
//                sum += nav.getAmount();
//                count++;
//            }
//        }
//        float averageAmount = (float) (sum / count);
        this.portfolioList = portfolioList;

        initChartView();
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

    private IPortfolioChart.Presenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new PortfolioChartPresenter(PortfolioChartFragmentTest2.this, UseCaseRxHandler.getInstance());
        }
        return mPresenter;
    }

    private void generateColumnData() {

        int numSubcolumns = 1;
        int numColumns = months.length;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
            }

            axisValues.add(new AxisValue(i).setLabel(months[i]));

            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

        vChartBot.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        vChartBot.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        vChartBot.setValueSelectionEnabled(true);

        vChartBot.setZoomType(ZoomType.HORIZONTAL);

        // vChartBot.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // SelectedValue sv = vChartBot.getSelectedValue();
        // if (!sv.isSet()) {
        // generateInitialLineData();
        // }
        //
        // }
        // });

    }

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialLineData() {
        //Equals number of days
        int numValues = 31;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for(int x = 0; x < portfolioList.size(); x++){
            for (int i = 0; i < numValues; i++) {
//                values.add(new PointValue(i, (float) Math.random() * 100f));
//            values.add(new PointValue(i, 0));
                values.add(new PointValue(i, portfolioList.get(x).getNavs().get(i).getAmount()));
                axisValues.add(new AxisValue(i).setLabel(String.valueOf(i + 1) + "th"));
            }
        }


        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        // prepare preview data, is better to use separate deep copy for preview chart.
        // Set color to grey to make preview area more visible.
        previewData = new LineChartData(lineData);
        for(int x = 0; x < previewData.getLines().size(); x++) {
            previewData.getLines().get(x).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
        }

        vChartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        vChartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 110, 6, 0);
        vChartTop.setMaximumViewport(v);
        vChartTop.setCurrentViewport(v);

        vChartTop.setZoomType(ZoomType.HORIZONTAL);

        vChartTop.setZoomEnabled(false);
        vChartTop.setScrollEnabled(false);

        vChartPreview.setLineChartData(lineData);
        vChartPreview.setViewportChangeListener(new ViewportListener());

//        previewX(false);
        previewXY();
        vChartPreview.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
    }

    private void generateLineData(int color, float range) {
        // Cancel last animation if not finished.
        vChartTop.cancelDataAnimation();

        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        for (PointValue value : line.getValues()) {
            // Change target only for Y value.
//            value.setTarget(value.getX(), value.getY());
            value.setTarget(value.getX(), (float) Math.random() * range);
        }

        // Start new data animation with 300ms duration;
        vChartTop.startDataAnimation(300);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            generateLineData(value.getColor(), 100);
        }

        @Override
        public void onValueDeselected() {

            generateLineData(ChartUtils.COLOR_GREEN, 0);

        }
    }

    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            vChartTop.setCurrentViewport(newViewport);
        }

    }

}
