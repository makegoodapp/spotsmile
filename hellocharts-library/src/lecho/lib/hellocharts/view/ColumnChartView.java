package lecho.lib.hellocharts.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SubColumnValue;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;

/**
 * ColumnChart/BarChart, supports subcolumns, stacked collumns and negative values.
 *
 * @author Leszek Wach
 */
public class ColumnChartView extends AbstractChartView implements ColumnChartDataProvider {
	private static final String TAG = "ColumnChartView";
	private ColumnChartData data;
	private ColumnChartOnValueSelectListener onValueTouchListener = new DummyColumnChartOnValueSelectListener();

	public ColumnChartView(Context context) {
		this(context, null, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new ColumnChartRenderer(context, this, this);
		setColumnChartData(ColumnChartData.generateDummyData());
	}

	@Override
	public ColumnChartData getColumnChartData() {
		return data;
	}

	@Override
	public void setColumnChartData(ColumnChartData data) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Setting data for ColumnChartView");
		}

		if (null == data) {
			this.data = ColumnChartData.generateDummyData();
		} else {
			this.data = data;
		}
		axesRenderer.initAxesAttributes();
		chartRenderer.initDataAttributes();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();

		ViewCompat.postInvalidateOnAnimation(ColumnChartView.this);

	}

	@Override
	public ColumnChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener() {
		SelectedValue selectedValue = chartRenderer.getSelectedValue();

		if (selectedValue.isSet()) {
			SubColumnValue value = data.getColumns().get(selectedValue.getFirstIndex()).getValues()
					.get(selectedValue.getSecondIndex());
			onValueTouchListener.onValueSelected(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(), value);
		} else {
			onValueTouchListener.onValueDeselected();
		}
	}

	public ColumnChartOnValueSelectListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(ColumnChartOnValueSelectListener touchListener) {
		if (null != touchListener) {
			this.onValueTouchListener = touchListener;
		}
	}
}
