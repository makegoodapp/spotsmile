package com.makegoodapps.spotsmile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makegoodapps.spotsmile.resultdatabase.MySQLiteHelper;
import com.makegoodapps.spotsmile.resultdatabase.ResultSource;
import com.makegoodapps.spotsmile.widget.RemovableAd;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubColumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A column and line chart
 * Created by WillsPC on 2014/12/30.
 */
public class ResultActivity extends ActionBarActivity {

    private ResultSource mResultSource = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
        mResultSource = new ResultSource(this);
        mResultSource.open();

        RemovableAd removableAd = (RemovableAd) findViewById(R.id.resultRemovableAd);
        if(mResultSource.isEmpty()) {
            removableAd.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mResultSource.open();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mResultSource.close();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private String[] mMonths;

        public final static String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
                "23", "24", "25", "26", "27", "28", "29", "30", "31",};

        private final static int LINE_CHART_Y_LIMIT = 120;
        private final static int LINE_CHART_X_LIMIT = 31;

        private LineChartView chartTop;
        private ColumnChartView chartBottom;

        private LineChartData lineData;
        private ColumnChartData columnData;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_line_column_dependency, container, false);

            mMonths = getActivity().getResources().getStringArray(R.array.month_list);

            // *** TOP LINE CHART ***
            chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
        }

        private void generateColumnData() {

            int numSubColumns = 1;
            int numColumns = mMonths.length;

            List<AxisValue> axisValues = new ArrayList<>();
            List<Column> columns = new ArrayList<>();
            List<SubColumnValue> values;
            ResultSource resultSource = new ResultSource(getActivity());
            resultSource.open();
            for (int i = 0; i < numColumns; ++i) {

                int month = i + 1;
                int monthCount = 0;
                int playDates = 0;
                Cursor monthCursor = resultSource.getMonthResult(month);
                if(monthCursor != null && monthCursor.moveToFirst()) {
                    do {
                        monthCount += monthCursor.getInt(monthCursor.getColumnIndex(MySQLiteHelper.COLUMN_RESULT));
                    } while (monthCursor.moveToNext());
                    playDates = monthCursor.getCount();
                    monthCursor.close();
                }
                resultSource.close();
                values = new ArrayList<>();
                for (int j = 0; j < numSubColumns; ++j) {
                    values.add(new SubColumnValue(playDates == 0 ? 0 : monthCount/playDates, ChartUtils.pickColor()));
                }

                axisValues.add(new AxisValue(i, mMonths[i].toCharArray()));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
            }

            columnData = new ColumnChartData(columns);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

            // chartBottom.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // SelectedValue sv = chartBottom.getSelectedValue();
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
            int numValues = 31;

            List<AxisValue> axisValues = new ArrayList<>();
            List<PointValue> values = new ArrayList<>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                axisValues.add(new AxisValue(i, days[i].toCharArray()));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<>();
            lines.add(line);

            lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewPort- remember to set viewPorts after data.
            Viewport v = new Viewport(0, LINE_CHART_Y_LIMIT, LINE_CHART_X_LIMIT, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        private void generateLineData(int color, float range) {
            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            // Modify data targets
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            for (PointValue value : line.getValues()) {
                // Change target only for Y value.
                value.setTarget(value.getX(), (float) Math.random() * range);
            }
            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private void setLineData(Cursor monthCursor, int color) {
            chartTop.cancelDataAnimation();
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            monthCursor.moveToFirst();
            do {
                int result = monthCursor.getInt(monthCursor.getColumnIndex(MySQLiteHelper.COLUMN_RESULT));
                int date = monthCursor.getInt(monthCursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE));
                PointValue value = line.getValues().get(date - 1);
                value.setTarget(value.getX(), result);
            } while (monthCursor.moveToNext());

            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subColumnIndex, SubColumnValue value) {
                generateLineData(ChartUtils.COLOR_GREEN, 0);
                ResultSource resultSource = new ResultSource(getActivity());
                resultSource.open();
                int month = columnIndex + 1;
                Cursor cur = resultSource.getMonthResult(month);
                if(cur != null && cur.moveToFirst()) {
                    setLineData(cur, value.getColor());
                    cur.close();
                }
                resultSource.close();
            }

            @Override
            public void onValueDeselected() {

                generateLineData(ChartUtils.COLOR_GREEN, 0);

            }
        }
    }

}
